package com.czy.post.rule;

public class RuleNumToString {

    public static String numToString(Long num) {
        if (num < 0) {
            return "Invalid number";
        }

        // 大于 1000M展示为 1B；比如105643909 -> 1.0B
        if (num >= 1_000_000_000) {
            return String.format("%.1fB", num / 1_000_000_000.0);
        }
        // 大于 1000k展示为 1M；比如105643 -> 1.0M
        else if (num >= 1_000_000) {
            return String.format("%.1fM", num / 1_000_000.0);
        }
        // 大于 1000k展示为 1K；比如1105 -> 1.1K
        else if (num >= 1_000) {
            return String.format("%.1fK", num / 1_000.0);
        } else {
            return num.toString();
        }
    }

    public static void main(String[] args) {
        Long num = 1056439090L;
        System.out.println(numToString(num));
    }

}
