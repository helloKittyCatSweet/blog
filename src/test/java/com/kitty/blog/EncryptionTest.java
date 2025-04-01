package com.kitty.blog;

import com.kitty.blog.utils.component.EncryptionUtil;

public class EncryptionTest {
    public static void main(String[] args) {
        // 使用示例密钥（实际生产环境请使用安全生成的密钥）
        String testKey = "pf46F20xY/x+owVHLxbSeA==";

        EncryptionUtil encryptionUtil = new EncryptionUtil();
        String email = "1448185924@qq.com";

        // 加密
        String encrypted = encryptionUtil.encrypt(email);
        System.out.println("加密后的值: " + encrypted);

        // 解密验证
        String decrypted = encryptionUtil.decrypt(encrypted);
        System.out.println("解密后的值: " + decrypted);
    }
}