package com.kitty.blog.domain.service.contentReview;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SecondaryMessageReviewerService {

    // 敏感词列表（可以根据需要扩展）
    private static final List<String> SENSITIVE_WORDS = List.of(
            "诈骗", "赌博", "色情", "非法", "广告", "推广", "破解", "破解版", "盗版"
    );

    // 特殊字符列表
    private static final List<Character> SPECIAL_CHARS = List.of(
            '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', '{', '}', '[', ']', '|', '\\', ':', ';', '"', '<', '>', ',', '.', '?', '/'
    );

    // 配置参数
    private static final int MAX_MESSAGE_LENGTH = 500;  // 消息最大长度
    private static final int MAX_LINKS = 3;             // 最大链接数
    private static final int MAX_SPECIAL_CHARS = 10;    // 最大特殊字符数
    private static final int MAX_CONSECUTIVE_CHARS = 5; // 最大连续相同字符数
    private static final int MAX_PARAGRAPHS = 5;        // 最大段落数

    /**
     * 审核消息是否可疑
     *
     * @param message 待审核的消息
     * @return 审核结果
     */
    public ReviewResult review(String message) {
        ReviewResult result = new ReviewResult();
        result.setSuspicious(false);
        result.setScore(0);

        // 1. 检查消息长度
        if (message.length() > MAX_MESSAGE_LENGTH) {
            result.setSuspicious(true);
            result.addReason("消息过长（超过" + MAX_MESSAGE_LENGTH + "字符）");
            result.setScore(result.getScore() + 20);
        }

        // 2. 检查链接数量和格式
        checkLinks(message, result);

        // 3. 检查特殊字符数量
        checkSpecialCharacters(message, result);

        // 4. 检查连续字符
        checkConsecutiveCharacters(message, result);

        // 5. 检查段落数量
        checkParagraphs(message, result);

        // 6. 检查消息结构异常
        checkMessageStructure(message, result);

        // 7. 检查敏感词（补充百度云可能未覆盖的敏感词）
        checkSensitiveWords(message, result);

        // 确保评分不超过100
        result.setScore(Math.min(result.getScore(), 100));

        return result;
    }

    /**
     * 检查链接数量和格式
     */
    private void checkLinks(String message, ReviewResult result) {
        // 检查链接数量
        int linkCount = countLinks(message);
        if (linkCount > MAX_LINKS) {
            result.setSuspicious(true);
            result.addReason("链接数量过多（" + linkCount + "个，限制为" + MAX_LINKS + "个）");
            result.setScore(result.getScore() + 15);
        }

        // 检查链接格式是否异常
        if (!checkLinkFormat(message)) {
            result.setSuspicious(true);
            result.addReason("链接格式异常");
            result.setScore(result.getScore() + 10);
        }
    }

    /**
     * 检查特殊字符数量
     */
    private void checkSpecialCharacters(String message, ReviewResult result) {
        int specialCharCount = countSpecialCharacters(message);
        if (specialCharCount > MAX_SPECIAL_CHARS) {
            result.setSuspicious(true);
            result.addReason("特殊字符过多（" + specialCharCount + "个，限制为" + MAX_SPECIAL_CHARS + "个）");
            result.setScore(result.getScore() + 10);
        }
    }

    /**
     * 检查连续字符
     */
    private void checkConsecutiveCharacters(String message, ReviewResult result) {
        if (hasConsecutiveCharacters(message, MAX_CONSECUTIVE_CHARS)) {
            result.setSuspicious(true);
            result.addReason("消息中包含过多连续相同字符");
            result.setScore(result.getScore() + 10);
        }
    }

    /**
     * 检查段落数量
     */
    private void checkParagraphs(String message, ReviewResult result) {
        int paragraphCount = countParagraphs(message);
        if (paragraphCount > MAX_PARAGRAPHS) {
            result.setSuspicious(true);
            result.addReason("消息段落过多（" + paragraphCount + "段，限制为" + MAX_PARAGRAPHS + "段）");
            result.setScore(result.getScore() + 10);
        }
    }

    /**
     * 检查消息结构异常
     */
    private void checkMessageStructure(String message, ReviewResult result) {
        // 检查消息是否包含异常格式（例如过多换行符）
        if (hasAbnormalStructure(message)) {
            result.setSuspicious(true);
            result.addReason("消息结构异常（例如过多换行符或空白字符）");
            result.setScore(result.getScore() + 10);
        }
    }

    /**
     * 检查敏感词
     */
    private void checkSensitiveWords(String message, ReviewResult result) {
        List<String> matchedWords = findSensitiveWords(message);
        if (!matchedWords.isEmpty()) {
            result.setSuspicious(true);
            result.addReason("包含敏感词: " + String.join(", ", matchedWords));
            result.setScore(result.getScore() + 30);
        }
    }

    /**
     * 统计链接数量
     */
    private int countLinks(String message) {
        Pattern pattern = Pattern.compile("http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\\(\\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+");
        Matcher matcher = pattern.matcher(message);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * 检查链接格式是否异常
     */
    private boolean checkLinkFormat(String message) {
        Pattern pattern = Pattern.compile("http[s]?://(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(?:/[^\\s]*)?");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String link = matcher.group();
            // 检查链接是否过长或包含异常字符
            if (link.length() > 100 || link.contains(" ")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 统计特殊字符数量
     */
    private int countSpecialCharacters(String message) {
        int count = 0;
        for (char c : message.toCharArray()) {
            if (SPECIAL_CHARS.contains(c)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 检查是否包含过多连续相同字符
     */
    private boolean hasConsecutiveCharacters(String message, int maxConsecutive) {
        int currentCount = 1;
        for (int i = 1; i < message.length(); i++) {
            if (message.charAt(i) == message.charAt(i - 1)) {
                currentCount++;
                if (currentCount > maxConsecutive) {
                    return true;
                }
            } else {
                currentCount = 1;
            }
        }
        return false;
    }

    /**
     * 统计段落数量
     */
    private int countParagraphs(String message) {
        Pattern pattern = Pattern.compile("\\n\\n");
        Matcher matcher = pattern.matcher(message);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count + 1; // 至少有一个段落
    }

    /**
     * 检查消息结构是否异常
     */
    private boolean hasAbnormalStructure(String message) {
        // 检查换行符数量是否过多
        long newlineCount = message.chars().filter(c -> c == '\n').count();
        if (newlineCount > message.length() / 5) { // 换行符占比超过20%
            return true;
        }

        // 检查空白字符是否过多
        long whitespaceCount = message.chars().filter(c -> Character.isWhitespace(c)).count();
        if (whitespaceCount > message.length() / 3) { // 空白字符占比超过33%
            return true;
        }

        return false;
    }

    /**
     * 查找消息中的敏感词
     */
    private List<String> findSensitiveWords(String message) {
        List<String> matchedWords = new ArrayList<>();
        for (String word : SENSITIVE_WORDS) {
            if (message.contains(word)) {
                matchedWords.add(word);
            }
        }
        return matchedWords;
    }

    /**
     * 审核结果类
     */
    @Getter
    public static class ReviewResult {
        @Setter
        private boolean suspicious;
        @Setter
        private int score;
        private final List<String> reasons;

        public ReviewResult() {
            this.reasons = new ArrayList<>();
        }

        public void addReason(String reason) {
            this.reasons.add(reason);
        }
    }
}