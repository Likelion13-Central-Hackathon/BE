package com.likelion.server.domain.answer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.server.domain.answer.entity.Answer;
import com.likelion.server.domain.answer.entity.Question;
import com.likelion.server.domain.answer.exception.AnswersNotFoundException;
import com.likelion.server.domain.answer.repository.AnswerRepository;
import com.likelion.server.domain.answer.repository.QuestionRepository;
import com.likelion.server.domain.answer.web.dto.QaResponse;
import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.exception.IdeaNotFoundException;
import com.likelion.server.infra.gpt.GptChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class QaServiceImpl implements QaService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final GptChatService gptChatService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<QaResponse> generateByAnswerId(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(AnswersNotFoundException::new);

        // 1) 프롬프트 구성
        String prompt = buildPrompt();

        // 2) GPT 호출
        String input = """
                [문항 질문]
                %s

                [사용자 답변]
                %s

                [AI 첨삭 답변]
                %s
                """.formatted(
                safe(answer.getQuestion()),
                safe(answer.getUserAnswer()),
                safe(answer.getAiAnswer())
        );

        String raw = gptChatService.chat(prompt, input);

        // 3) JSON 파싱
        String json = stripCodeFence(raw);

        List<QaResponse> items;
        try {
            items = objectMapper.readValue(json, new TypeReference<List<QaResponse>>() {});
        } catch (Exception e) {
            items = fallbackToSingle(raw);
        }

        // 4) 저장 (questions)
        List<Question> entities = new ArrayList<>();
        for (QaResponse it : items) {
            Question q = Question.builder()
                    .answer(answer)
                    .question(it.question())
                    .answerText(it.answer())
                    .build();
            entities.add(q);
        }
        questionRepository.saveAll(entities);

        return items;
    }

    private String buildPrompt() {
        return """
                당신은 정부 창업지원사업 평가를 잘 아는 멘토입니다.
                아래 '문항 질문/사용자 답변/AI 첨삭 답변'을 보고, 심사 시 실제로 나올 법한 질의응답을 생성하세요.
                - 평문으로만 작성하세요. 굵게, 기울임 등 마크다운 금지.
                - 반드시 JSON 배열로만 출력하세요. 코드블록(```) 금지.
                - 각 항목은 {"question": "...", "answer": "..."} 형식.
                - 4개 항목을 생성.
                - 질문은 구체적이고 검증 가능한 포인트를 찌르세요.
                - 답변은 실무적으로 설득력 있게, 간결한 근거 포함.
                - 마지막 문장은 마침표로 끝내세요.
                """;
    }

    private static String stripCodeFence(String s) {
        if (s == null) return "[]";
        String t = s.trim();
        if (t.startsWith("```")) {
            int first = t.indexOf('\n');
            int last = t.lastIndexOf("```");
            if (first >= 0 && last > first) t = t.substring(first + 1, last).trim();
        }
        int start = Math.max(t.indexOf('['), 0);
        int end = Math.max(t.lastIndexOf(']') + 1, t.length());
        return t.substring(start, end);
    }

    private static String safe(String s) {
        return Optional.ofNullable(s).orElse("");
    }

    private static List<QaResponse> fallbackToSingle(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of(new QaResponse("핵심 리스크는 무엇이며 어떻게 관리하나요?", "핵심 리스크를 식별하고 정량 지표로 모니터링하며, 사전/사후 대응 방안을 문서화하여 실행합니다."));
        }
        String[] lines = raw.split("\\r?\\n");
        String q = lines[0].replaceAll("^[Qq][.:)\\s-]*", "").trim();
        String a = raw.substring(Math.min(lines[0].length(), raw.length())).trim();
        if (q.isEmpty()) q = "핵심 리스크는 무엇이며 어떻게 관리하나요?";
        if (a.isEmpty()) a = "핵심 리스크를 식별하고 정량 지표로 모니터링하며, 사전/사후 대응 방안을 문서화하여 실행합니다.";
        return List.of(new QaResponse(q, a));
    }
}
