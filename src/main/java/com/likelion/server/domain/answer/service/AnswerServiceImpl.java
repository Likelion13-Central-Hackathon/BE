package com.likelion.server.domain.answer.service;

import com.likelion.server.domain.answer.entity.Answer;
import com.likelion.server.domain.answer.entity.BusinessPlanQuestion;
import com.likelion.server.domain.answer.repository.AnswerRepository;
import com.likelion.server.infra.gpt.GptChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final GptChatService gptChatService;

    @Override
    public AnswerResult correctQuestion(int questionNumber, String userAnswer) {
        var qEnum = BusinessPlanQuestion.fromNumber(questionNumber);
        String prompt = buildPrompt(questionNumber, qEnum.getTitle());

        String ai = gptChatService.chat(prompt, userAnswer);

        Answer saved = answerRepository.save(
                Answer.builder()
                        .number(qEnum.getNumber())
                        .question(qEnum.getTitle())
                        .userAnswer(userAnswer)
                        .aiAnswer(ai)
                        .build()
        );

        return new AnswerResult(saved.getId(), ai);
    }

    private String buildPrompt(int number, String title) {
        return """
               당신은 대한민국 정부 창업지원사업 심사위원 시각으로 사업계획서 첨삭을 전문으로 하는 멘토입니다.
               문항(%d: %s)에 대한 사용자 답변을 반드시 '첨삭' 형태로 개선해 주세요.
               - 굵은 글씨, 기울임, 마크다운 등 서식 표시는 사용하지 마세요.
               - 평문으로만 작성하세요.
               - 시장성/차별점/실행계획/근거/지표를 보완 문장으로 제시
               - 600~900자 내외
               """.formatted(number, title);
    }
}
