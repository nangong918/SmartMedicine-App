package com.czy.api.constant.post;

/**
 * @author 13225
 * @date 2025/5/7 11:20
 */
public enum DiseasesKnowledgeGraphEnum {

    /**
     *             "checks.json",
     *             "departments.json",
     *             "diseases.json",
     *             "drugs.json",
     *             "foods.json",
     *             "producers.json",
     *             "recipes.json",
     */

    NULL(0, "NULL"),
    CHECKS(1, "checks"),
    DEPARTMENTS(2, "departments"),
    DISEASES(3, "diseases"),
    DRUGS(4, "drugs"),
    FOODS(5, "foods"),
    PRODUCERS(6, "producers"),
    RECIPES(7, "recipes");

    private final int value;
    private final String name;

    DiseasesKnowledgeGraphEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    // value -> o
    public static DiseasesKnowledgeGraphEnum getEnumByValue(int value) {
        for (DiseasesKnowledgeGraphEnum o : values()) {
            if (o.value == value) {
                return o;
            }
        }
        return NULL;
    }

}
