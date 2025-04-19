package com.kitty.blog.infrastructure.converter;

public class StringConverter {
    /**
     * 将输入字符串中的大写英文字母转换为小写，中文和数字保持不变
     * @param input 输入的字符串
     * @return 转换后的字符串
     */
    public static String convertUpperCaseToLowerCase(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                // 将大写字母转换为小写字母
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}