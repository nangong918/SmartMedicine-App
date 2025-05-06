package service;


import com.alibaba.fastjson.JSONArray;
import com.czy.api.domain.ao.post.AcTreeInfo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.post.PostServiceApplication;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author 13225
 * @date 2025/1/13 15:32
 */

@Slf4j
@SpringBootTest(classes = PostServiceApplication.class)
@TestPropertySource("classpath:application.properties")
public class ServiceTests {

    private static HashMap<String, String> loadData(@NonNull String type, String path) throws Exception{
        HashMap<String, String> map = new HashMap<>();
        StringBuilder jsonString = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(path).toAbsolutePath().toString()))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
        }

        // 使用 fastjson 解析 JSON
        JSONArray jsonArray = JSONArray.parseArray(jsonString.toString());
        for (int i = 0; i < jsonArray.size(); i++) {
            String v = jsonArray.getString(i);
            map.put(v, type);
        }
        return map;
    }
    private final static String[] filePaths = {
            "checks.json",
            "departments.json",
            "diseases.json",
            "drugs.json",
            "foods.json",
            "producers.json",
            "recipes.json",
//            "symptoms.json",
    };
    private static String getAbsolutePath(String relativePath){
        // 获取当前项目的绝对路径
        String currentDir = System.getProperty("user.dir");
        // D:\CodeLearning\smart-medicine
        String projectRoot = Paths.get(currentDir).toString(); // 获取根目录
        return Paths.get(projectRoot, relativePath).toString();
    }

    private static final String testStr1 = "嗅觉障碍是怎么造成的？是由于月经不调吗？吃鸡蛋阿莫西林能治疗好吗？";
    private static final String testStr2 = "嗅觉障碍是怎么造成的？是由于月经不调吗？吃鸡蛋阿莫西林能治疗好吗？嗅觉障碍是怎么造成的？是由于月经不调吗？吃鸡蛋阿莫西林能治疗好吗？" +
            "嗅觉障碍是怎么造成的？是由于月经不调吗？吃鸡蛋阿莫西林能治疗好吗？嗅觉障碍是怎么造成的？" +
            "是由于月经不调吗？吃鸡蛋阿莫西林能治疗好吗？嗅觉障碍是怎么造成的？是由于月经不调吗？" +
            "吃鸡蛋阿莫西林能治疗好吗？嗅觉障碍是怎么造成的？是由于月经不调吗？吃鸡蛋阿莫西林能治疗好吗？" +
            "嗅觉障碍是怎么造成的？是由于月经不调吗？吃鸡蛋阿莫西林能治疗好吗？嗅觉障碍是怎么造成的？" +
            "是由于月经不调吗？吃鸡蛋阿莫西林能治疗好吗？嗅觉障碍是怎么造成的？是由于月经不调吗？" +
            "1. 检查依赖项和插件的版本\n" +
            "首先，确保你的 build.gradle 文件中定义的依赖项和插件版本是正确的。有时，错误的版本号或拼写错误可能导致解析失败。检查所有依赖项和插件的版本号，并与官方文档或仓库中的可用版本进行对比。\n" +
            "\n" +
            "2. 清理和重新构建项目\n" +
            "在 Android Studio 中，你可以尝试执行以下步骤来清理和重新构建项目：\n" +
            "\n" +
            "清理项目：点击菜单栏中的 Build > Clean Project。\n" +
            "重新构建项目：接着，点击 Build > Rebuild Project。\n" +
            "这可以清除旧的构建缓存，并尝试重新下载和解析依赖项。\n" +
            "\n" +
            "3. 检查网络连接\n" +
            "‘Could not resolve’ 错误可能是由于网络问题导致的。确保你的计算机连接到互联网，并且没有任何防火墙或代理设置阻止了 Gradle 访问依赖项仓库。\n" +
            "\n" +
            "4. 更新 Gradle 版本\n" +
            "有时，使用较旧的 Gradle 版本可能会导致依赖项解析问题。尝试更新你的 gradle-wrapper.properties 文件中的 Gradle 版本到最新的稳定版本，并重新构建项目。\n" +
            "\n" +
            "5. 手动添加依赖项\n" +
            "如果某个特定的依赖项无法解析，你可以尝试手动下载该依赖项的 JAR 文件或 AAR 文件，并将其放在项目的 libs 文件夹中。然后，在 build.gradle 文件中添加对该依赖项的引用。\n" +
            "\n" +
            "6. 检查依赖项冲突\n" +
            "有时，项目中的不同模块可能依赖于相同库的不同版本，导致冲突。你可以使用 Gradle 的依赖项分析功能来检查和解决这些冲突。在命令行中运行以下命令：\n" +
            "\n" +
            "./gradlew app:dependencies\n" +
            "这将列出项目的所有依赖项和它们的版本。检查是否有任何冲突的依赖项，并尝试解决它们。\n" +
            "\n" +
            "7. 清理 Gradle 缓存\n" +
            "Gradle 会将下载的依赖项和插件缓存在用户主目录下的 .gradle/caches 文件夹中。有时，这些缓存可能会导致解析问题。你可以尝试手动删除该文件夹，并重新构建项目，让 Gradle 重新下载依赖项和插件。\n" +
            "\n" +
            "总结\n" +
            "‘Could not resolve’ 错误通常与依赖项和插件的解析有关。通过检查配置、清理和重新构建项目、更新 Gradle 版本、手动添加依赖项、解决依赖项冲突和清理 Gradle 缓存等方法，你通常可以解决这个问题。如果以上方法都不起作用，可能需要进一步检查项目的配置和网络设置。CSDN首页\n" +
            "博客\n" +
            "下载\n" +
            "学习\n" +
            "社区\n" +
            "GitCode\n" +
            "InsCodeAI\n" +
            "会议\n" +
            "java ahocorasick\n" +
            " 搜索 C 知道\n" +
            "\n" +
            "会员中心 \n" +
            "消息\n" +
            "历史\n" +
            "创作中心\n" +
            "创作\n" +
            "Java Aho Corasick 自动机的使用\n" +
            "\n" +
            "谈谈1974\n" +
            "\n" +
            "于 2022-12-24 15:28:03 发布\n" +
            "\n" +
            "阅读量1.3k\n" +
            " 收藏\n" +
            "\n" +
            "点赞数\n" +
            "分类专栏： Java 基础\n" +
            "版权\n" +
            "\n" +
            "\n" +
            "java\n" +
            "开发语言\n" +
            "数据结构\n" +
            "\n" +
            "Java 基础\n" +
            "专栏收录该内容\n" +
            "37 篇文章\n" +
            "订阅专栏\n" +
            "文章目录\n" +
            "1. 前言\n" +
            "2. 依赖引入\n" +
            "3. 使用示例\n" +
            "1. 前言\n" +
            "AC 自动机是一种用于字符串多模式匹配的算法，其算法实现的底层数据结构多为字典树，其中一种实现的具体信息读者可参考 Aho Corasick 自动机结合 DoubleArrayTrie 极速多模式匹配。在实际开发中 AC 自动机常用于关键词识别提取的场景，以下是相关使用示例\n" +
            "\n" +
            "2. 依赖引入\n" +
            "首先引入双数组字典树相关依赖，读者如有兴趣可前往 maven 仓库自行查找相关版本\n" +
            "\n" +
            "     <dependency>\n" +
            "            <groupId>com.hankcs</groupId>\n" +
            "            <artifactId>aho-corasick-double-array-trie</artifactId>\n" +
            "            <version>1.2.3</version>\n" +
            "     </dependency>\n" +
            "1\n" +
            "2\n" +
            "3\n" +
            "4\n" +
            "5\n" +
            "3. 使用示例\n" +
            "AhoCorasickDoubleArrayTrie 的使用非常轻便，大致可分为以下几步，只要构建字典的语料足够丰富即可达到较高的识别准确率\n" +
            "\n" +
            "数据准备\n" +
            "简单来说，就是构建字典树的数据的准备，只要按照 Map 结构存储即可\n" +
            "字典树构建\n" +
            "字典树构建只需要调用 AhoCorasickDoubleArrayTrie 提供的方法即可\n" +
            "关键词识别使用\n" +
            "调用 AhoCorasickDoubleArrayTrie 提供的方法识别目标字符串，同时指定结果处理函数即可\n" +
            "    public static void main(String[] args) {\n" +
            "        // 字典数据准备\n" +
            "        TreeMap<String, String> map = new TreeMap<>();\n" +
            "        map.put( \"hers\",  \"hers\");\n" +
            "        map.put( \"his\",  \"his\");\n" +
            "        map.put( \"she\",  \"she\");\n" +
            "        map.put( \"he\",  \"he\");\n" +
            "        // 字典树构建\n" +
            "        AhoCorasickDoubleArrayTrie<String> act = new AhoCorasickDoubleArrayTrie<>();\n" +
            "        act.build(map);\n" +
            "        // 关键词提取\n" +
            "        act.parseText(\"uhers\", (begin, end, value) -> {\n" +
            "            System.out.printf(\"[%d:%d]=%s\\n\", begin, end, value);\n" +
            "        });\n" +
            "    }\n" +
            "1\n" +
            "2\n" +
            "3\n" +
            "4\n" +
            "5\n" +
            "6\n" +
            "7\n" +
            "8\n" +
            "9\n" +
            "10\n" +
            "11\n" +
            "12\n" +
            "13\n" +
            "14\n" +
            "15\n" +
            "\n" +
            "谈谈1974\n" +
            "关注\n" +
            "\n" +
            "0\n" +
            "\n" +
            "\n" +
            "0\n" +
            "\n" +
            "0\n" +
            "\n" +
            "分享\n" +
            "\n" +
            "专栏目录\n" +
            "\n" +
            "java实现多模匹配算法_多模字符串匹配算法-Aho–Corasick\n" +
            "weixin_42512903的博客\n" +
            " 640\n" +
            "背景在做实际工作中，最简单也最常用的一种自然语言处理方法就是关键词匹配，例如我们要对n条文本进行过滤，那本身是一个过滤词表的，通常进行过滤的代码如下for (String document : documents) {for (String filterWord : filterWords) {if (document.contains(filterWord)) {//process ...}}}...\n" +
            "AC自动机-2(AhoCorasickDoubleArrayTrie)\n" +
            "4-25\n" +
            "AC自动机-2(AhoCorasickDoubleArrayTrie) Aho-Corasick DoubleArrayTrie (AC DAT) 是一种结合了Aho-Corasick算法和Double Array Trie的数据结构,DAT保证了较高的存储效率,AC保证了多模式字符串匹配效率。 一个经典的实现是hanlp的Java实现:AhoCorasickDoubleArrayTrie。 主要构造过程如下: publicvoidbuild(Map<String, ...\n" +
            "Aho-Corasick算法的Java实现与分析_aho-corasick算法 java\n" +
            "4-21\n" +
            "org.ahocorasick.trie包 这里封装了Trie树,其中比较重要的类是Trie树的节点State: 我重构了State,将其异化为UnicodeState和AsciiState类。其中UnicodeState类使用 Map<Character, State> 来储存goto表,而AsciiState类使用数组 State[] success = new State[256]来储存,这样在Ascii表上面,AsciiState的匹配要稍微快一些,...\n" +
            "AC自动机详解：高效多模式字符串匹配\n" +
            "最新发布\n" +
            "xiaoyu❅的博客\n" +
            " 1105\n" +
            "AC自动机（Aho-Corasick算法）是一种用于多模式字符串匹配的高效算法，广泛应用于敏感词过滤、文本搜索等领域。本文将深入探讨AC自动机的工作原理、构建过程以及如何使用Java语言来实现这一强大的算法。\n" +
            "aho-corasick:Aho-Corasick算法的Java实现，可实现高效的字符串匹配\n" +
            "05-13\n" +
            "阿霍·科拉西克（Aho-Corasick） 相依性 在您的POM中包括此依赖项。 确保在Maven Central中检查最新版本。 < dependency> < groupId>org.ahocorasick</ groupId> < artifactId>ahocorasick</ artifactId> < version>0.6.3</ version> </ dependency> 介绍 大多数自由文本搜索都基于类似于Lucene的方法，其中，搜索文本被解析成其各个组成部分。 对于每个关键字，都会进行查找以查看其发生位置。 当寻找几个关键字时，这种方法很好，但是当搜索100,000个单词时，这种方法非常慢（例如，检查字典）。 Aho-Corasick算法在查找多个单词时会发光。 它没有使用所有关键字来构建结构，而不是将搜索文本切碎。 关键的Aho-C\n" +
            "多模式匹配 ahocorasick_hancks javaahocorasick\n" +
            "5-1\n" +
            "Trie树概述    Trie树,又称字典树、前缀树、单词查找树、键树,是一种多叉树形结构,是一种哈希树的变种。Trie这个术语来自于retrieval,发音为/tri:/ “tree”,也有人读为/traɪ/ “try”。Trie树典型应用是用于快速检索(最长前缀匹配),统计,排序和保存大量的字符串,所以经常被搜索引擎系统用于文本词频统...\n" +
            "Java / Scala - Trie 树简介与应用实现_java的trie\n" +
            "4-24\n" +
            "2.Java / Scala 实现 2.1Pom 依赖 <!-- https://mvnrepository.com/artifact/org.ahocorasick/ahocorasick --> <dependency> <groupId>org.ahocorasick</groupId> <artifactId>ahocorasick</artifactId> <version>0.6.3</version> </dependency> 2.2关键词匹配 ...\n" +
            "敏感词过滤的算法原理之 Aho-Corasick 算法\n" +
            "weixin_30550081的博客\n" +
            " 355\n" +
            "参考文档 http://www.hankcs.com/program/algorithm/implementation-and-analysis-of-aho-corasick-algorithm-in-java.html 简介 Aho-Corasick算法简称AC算法，通过将模式串预处理为确定有限状态自动机，扫描文本一遍就能结束。其复杂度为O(n)，即与模式串的数量和长度无关。...\n" +
            "ahocorasick:使用Java中的Hashmap轻松实现多模式字符串匹配算法（AhoCorasick）\n" +
            "05-14\n" +
            "Ahocorasick 使用Java中的Hashmap轻松实现多模式字符串匹配算法（AhoCorasick） 该项目是使用带有Java SE的eclipse完成的。 要使用它，只需将其导入到Eclipse中即可。 项目状态：完成\n" +
            "java找字符串不同的字符串_Java一次(或以最有效的方式)替换字符串中...\n" +
            "4-24\n" +
            "1,000:10使用1,000个字符和10个匹配的字符串替换:testStringUtils: 0秒,7毫秒testBorAhoCorasick: 0秒,19毫秒对于短字符串,设置Aho-Corasick的开销会使蛮力方法黯然失色StringUtils.replaceEach。基于文本长度的混合方法是可能的,以获得两种实现的最佳效果。实现考虑比较长度超过1 MB的文本的其他实现,包括:https://...\n" +
            "完蛋!我被 Out of Memory 包围了!_idea编译一直卡住 然后内存溢出-CSDN...\n" +
            "4-12\n" +
            "java.lang.OutOfMemoryError: Java heapspace 分析解决起来无非是那几步: dump 堆内存 通过MAT、YourKit、JProfiler 、IDEA Profiler 等一系列工具分析dump文件 找到占用内存最多、最大的对象,看看是哪个小可爱干的 分析代码,尝试优化代码、减少对象创建\n" +
            "ac算法 java_Aho-Corasick算法的Java实现与分析\n" +
            "weixin_35786770的博客\n" +
            " 543\n" +
            "简介Aho-Corasick算法简称AC算法，通过将模式串预处理为确定有限状态自动机，扫描文本一遍就能结束。其复杂度为O(n)，即与模式串的数量和长度无关。思想自动机按照文本字符顺序，接受字符，并发生状态转移。这些状态缓存了“按照字符转移成功(但不是模式串的结尾)”、“按照字符转移成功(是模式串的结尾)”、“按照字符转移失败”三种情况下的跳转与输出情况，因而降低了复杂度。基本构造AC算法中有三个核...\n" +
            "Aho-Corasick：高效多关键字搜索算法的Java实现\n" +
            "gitblog_00040的博客\n" +
            " 662\n" +
            "Aho-Corasick：高效多关键字搜索算法的Java实现 aho-corasickJava implementation of the Aho-Corasick algorithm for efficient string matching项目地址:https://gitcode.com/gh_mirrors/aho/aho-corasick 项目介绍 Aho-Corasick是一个高效的字...\n" +
            "高性能AC算法多关键词匹配文本功能Java实现_java 实现word文档智能匹 ...\n" +
            "4-17\n" +
            "*@author: <发哥讲Java-694204477@qq.com> *@create: 2023-09-19 17:20 **/ @Data publicclassAhoCorasick{ privateTrieNode root; publicAhoCorasick(){ root =newTrieNode(); } publicvoidaddKeyword(String keyword){ TrieNodecurrent=root; for(charch : keyword.toCharArray()) { ...\n" +
            "ac算法 java_java使用ac算法实现关键词高亮\n" +
            "4-20\n" +
            "import com.hankcs.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie; import java.util.ArrayList; import java.util.Arrays; import java.util.List; import java.util.TreeMap; public class KeywordMatch { /** * 构建ac自动机 */ public static AhoCorasickDoubleArrayTrie buildAcdt(List keywords){ ...\n" +
            "深入理解Aho-Corasick自动机算法1\n" +
            "08-03\n" +
            "**深入理解Aho-Corasick自动机算法** Aho-Corasick自动机（简称AC自动机）是一种在字符串搜索中非常高效的算法，尤其在处理多个模式串匹配时。该算法由Aho和Corasick在1975年提出，它结合了Trie树和KMP算法的优点，...\n" +
            "Aho Corasick自动机结合DoubleArrayTrie极速多模式匹配\n" +
            "weixin_34360651的博客\n" +
            " 1791\n" +
            "2019独角兽企业重金招聘Python工程师标准>>> ...\n" +
            "java恶意代码检测源码-ac:带有双阵列Trie的Aho-Corasick自动机（go中的多模式替代）\n" +
            "06-05\n" +
            "Aho-Corasick Automaton with Double Array Trie (Multi-patterm substitute in go) 多模式匹配（替换）具有很强的现实意义与实用价值：敏感词过滤，病毒特征码搜索等。 字典中约有一千条关键字，分为三大类：电影、...\n" +
            "Aho-Corasick自动机实现\n" +
            "10-23\n" +
            "Aho-Corasick自动机（AC自动机）是一种在文本搜索中非常高效的算法，由艾兹格·阿霍（Aho）和莫里斯·科拉斯克（Morris Corasick）于1975年提出。它扩展了KMP算法，能够一次性查找多个模式字符串，避免了对每个模式...\n" +
            "AhoCorasick:Aho Corasick Java 实现\n" +
            "06-13\n" +
            "阿霍科拉西克 在 Java 中实现 Aho-Corasick 算法。 该项目包含两个包： 树：包含节点的声明 ahocorasickimplementation：包含 aho-corasick 的实现和树的创建\n" +
            "Aho-Corasick算法的Java实现与分析1\n" +
            "08-03\n" +
            "简介Aho-Corasick算法简称AC算法，通过将模式串预处理为确定有限状态动机，扫描本遍就能结束。其复杂度为O(n)，即与模式串的数量和长度关。思想动机按照\n" +
            "java笔试题算法-aho-corasick:DannyYoo在Java中实现的Aho-Corasick算法，几乎没有改进\n" +
            "06-03\n" +
            "java笔试题算法\n" +
            "基于Aho-Corasick算法的Ahocorasick库的使用\n" +
            "qq_52965253的博客\n" +
            " 2489\n" +
            "由于最近项目中需要使用Python的库进行模式匹配，因此记录该库所使用的到的算法（AC算法）的概要以及该库在Python中的具体使用方法。算法是多模式匹配中的经典算法，目前在实际应用中较多。算法对应的数据结构是自动机，简称AC自动机Automaton。该算法能够识别出一个给定的语句中包含了哪些词典库中特定的词语，具有很不错的模式匹配作用。\n" +
            "探索高效字符串匹配的新天地 —— Aho-Corasick Java 实现解析与应用\n" +
            "gitblog_00004的博客\n" +
            " 538\n" +
            "探索高效字符串匹配的新天地 —— Aho-Corasick Java 实现解析与应用 去发现同类优质开源项目:https://gitcode.com/ 在文本处理的浩瀚宇宙中，快速且精确地进行字符串匹配是一项核心能力。今天，我们要向您隆重推荐一款基于Aho-Corasick算法的Java实现——一个专门为提升文本搜索效率而生的开源项目。 项目介绍 Aho-Corasick 是一款高度优化的Java...\n" +
            "ahocorasick库的简单使用\n" +
            "qq_38423499的博客\n" +
            " 1836\n" +
            "ahocorasick库的简单使用欢迎使用Markdown编辑器 欢迎使用Markdown编辑器 ahocorasick库主要用于关键字的匹配的，字符长度大概在10-20个之间。 看一下下面的例子就明白了。 wordlist= ['长春海外制药接骨续筋片', '香菇炖甲鱼', '三鹤药业黄柏胶囊', '上海衡山熊去氧胆酸片', '升和药业依托泊苷注射液', '怡诺思', '人格障碍', '转铁蛋白饱和度', '脾囊肿', '素烧白萝卜', '利君现代冠脉宁片', '上海复华药业注射用还原型谷', '阴囊上\n" +
            ".NET下AhoCorasick使用示例\n" +
            "极客神殿\n" +
            " 340\n" +
            "KeyFilter.cs using System; using System.Collections.Generic; using System.Linq; using System.Web; using System.Web.UI; using System.Web.UI.WebControls; public partial class KeyFilter : System.Web.UI.Page { protected void Page_Load(object sender, Event\n" +
            "pyahocorasick使用(ac自动机)\n" +
            "XD的博客\n" +
            " 2284\n" +
            "一、简介 pyahocorasick是一个快速高效的库，用于精确或近似的多模式字符串搜索，这意味着您可以在一些输入文本中同时找到多个关键字字符串。字符串“索引”可以提前构建，并保存（作为pickle）到磁盘，以便以后重新使用。该库提供了一个ahocarasick Python模块，您可以将其用作Trie之类的普通dict，或者将Trie转换为自动机，以实现高效的Aho-Carasick搜索。 二、安装 pip install pyahocorasick 三、使用 1.新建自动机 可以将Automaton类\n" +
            "关于我们\n" +
            "招贤纳士\n" +
            "商务合作\n" +
            "寻求报道\n" +
            "\n" +
            "400-660-0108\n" +
            "\n" +
            "kefu@csdn.net\n" +
            "\n" +
            "在线客服\n" +
            "工作时间 8:30-22:00\n" +
            "公安备案号11010502030143\n" +
            "京ICP备19004658号\n" +
            "京网文〔2020〕1039-165号\n" +
            "经营性网站备案信息\n" +
            "北京互联网违法和不良信息举报中心\n" +
            "家长监护\n" +
            "网络110报警服务\n" +
            "中国互联网举报中心\n" +
            "Chrome商店下载\n" +
            "账号管理规范\n" +
            "版权与免责声明\n" +
            "版权申诉\n" +
            "出版物许可证\n" +
            "营业执照\n" +
            "©1999-2025北京创新乐知网络技术有限公司\n" +
            "\n" +
            "谈谈1974\n" +
            "博客等级 \n" +
            "\n" +
            "码龄6年\n" +
            "283\n" +
            "原创\n" +
            "779\n" +
            "点赞\n" +
            "2218\n" +
            "收藏\n" +
            "432\n" +
            "粉丝\n" +
            "关注\n" +
            "私信\n" +
            "热门文章\n" +
            "MyBatis-plus 批量插入的通用方法使用  74639\n" +
            "Docker 使用-将容器打成镜像  50288\n" +
            "java.lang.OutOfMemoryError: Metaspace 的解决  37337\n" +
            "排查 reactor-netty 报错 Connection reset by peer 的过程  24474\n" +
            "MySQL 数据库统计函数 COUNT  20335\n" +
            "最新评论\n" +
            "Java 值传递与“引用传递”\n" +
            "浪迹天涯的贺: 牛的表情包表情包表情包\n" +
            "\n" +
            "Java agent 探针技术(1)-JVM 启动时 premain 进行类加载期增强\n" +
            "qq_22946469: 不知道你还用不用csdn agentmain 我研究出来也可以新加方法 只需要目标项目jdk启动的时候加-XX:+AllowRedefinitionToAddDeleteMethods 这个参数\n" +
            "\n" +
            "2022，在水一方\n" +
            "阿J~: 看到文章昏昏欲睡的我瞬间来了精神，必须支持\n" +
            "\n" +
            "FreeSWITCH 智能呼叫流程设计\n" +
            "时间@遗失: 大佬问一下这个有完整的源码，想学习一下\n" +
            "\n" +
            "FreeSWITCH 1.10 源码阅读(7)-uuid_bridge 命令原理解析\n" +
            "　BitDance: 能否沟通下呢 大佬 加个联系方式\n" +
            "\n" +
            "大家在看\n" +
            "3206. 交替组 I  693\n" +
            "3178. 找出 K 秒后拿着球的孩子  558\n" +
            "基于CNN的食物图像分类：最优模型测试与应用实战\n" +
            "3174. 清除数字  1039\n" +
            "3162. 优质数对的总数 I  560\n" +
            "最新文章\n" +
            "2023，所谓伊人\n" +
            "FreeSWITCH 1.10 源码阅读(7)-uuid_bridge 命令原理解析\n" +
            "FreeSWITCH 使用指北(2)-多段音频顺序播放的设置\n" +
            "2024年1篇2023年30篇2022年49篇2021年49篇2020年69篇2019年85篇\n" +
            "\n" +
            "目录\n" +
            "文章目录\n" +
            "1. 前言\n" +
            "2. 依赖引入\n" +
            "3. 使用示例\n" +
            "\n" +
            "分类专栏\n" +
            "\n" +
            "SIP\n" +
            "10篇\n" +
            "\n" +
            "Kafka 源码笔记\n" +
            "12篇\n" +
            "\n" +
            "Redis 源码阅读笔记\n" +
            "13篇\n" +
            "\n" +
            "Java 并发\n" +
            "7篇\n" +
            "\n" +
            "Spring 使用及备忘\n" +
            "10篇\n" +
            "\n" +
            "Spring 源码分析\n" +
            "22篇\n" +
            "\n" +
            "规范设计\n" +
            "8篇\n" +
            "\n" +
            "Tomcat 源码分析\n" +
            "5篇\n" +
            "\n" +
            "Apollo 配置中心源码分析\n" +
            "2篇\n" +
            "\n" +
            "Netty 源码分析\n" +
            "6篇\n" +
            "\n" +
            "FreeSWITCH 源码及使用\n" +
            "12篇\n" +
            "\n" +
            "MyBatis\n" +
            "12篇\n" +
            "\n" +
            "JVM\n" +
            "18篇\n" +
            "\n" +
            "Java 基础\n" +
            "37篇\n" +
            "\n" +
            "Linux\n" +
            "9篇\n" +
            "\n" +
            "Redis\n" +
            "19篇\n" +
            "\n" +
            "分布式\n" +
            "14篇\n" +
            "\n" +
            "数据库\n" +
            "12篇\n" +
            "\n" +
            "工具配置\n" +
            "21篇\n" +
            "\n" +
            "Web 基础\n" +
            "19篇\n" +
            "\n" +
            "细节备忘\n" +
            "7篇\n" +
            "\n" +
            "随笔\n" +
            "18篇\n" +
            "\n" +
            "IDEA\n" +
            "7篇\n" +
            "\n" +
            "网络安全\n" +
            "2篇\n" +
            "\n" +
            "缓存\n" +
            "4篇\n" +
            "\n" +
            "树\n" +
            "7篇\n" +
            "\n" +
            "Android Telephony\n" +
            "5篇\n" +
            "\n" +
            "算法\n" +
            "48篇\n" +
            "\n" +
            "数组\n" +
            "20篇\n" +
            "\n" +
            "双指针\n" +
            "12篇\n" +
            "\n" +
            "字符串\n" +
            "4篇\n" +
            "\n" +
            "动态规划\n" +
            "8篇\n" +
            "\n" +
            "回溯\n" +
            "4篇\n" +
            "\n" +
            "排序\n" +
            "5篇\n" +
            "\n" +
            "链表\n" +
            "9篇\n" +
            "\n" +
            "栈\n" +
            "感冒";

    @SneakyThrows
    public static void main(String[] args) {
        HashMap<String, AcTreeInfo> acTreeMap = new HashMap<>();
        HashMap<String, String> map = new HashMap<>();
        for (String filePath : filePaths){
            String relativePath = "/files/build_kg/" + filePath;
            String absolutePath = getAbsolutePath(relativePath);
            String type = filePath.split("\\.")[0];
            map = loadData(type, absolutePath);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                AcTreeInfo acTreeInfo = new AcTreeInfo(key, value);
                acTreeMap.put(key, acTreeInfo);
            }
        }
        String testKey = acTreeMap.entrySet().iterator().next().getKey();
        AcTreeInfo testValue = acTreeMap.entrySet().iterator().next().getValue();
        log.info("testKey：{}，testValue.key:{},testValue.value:{}", testKey, testValue.getKey(), testValue.getValue());
        AhoCorasickDoubleArrayTrie<AcTreeInfo> acTree = new AhoCorasickDoubleArrayTrie<>();
        acTree.build(acTreeMap);

        long start = System.currentTimeMillis();
        List<PostNerResult> results = getPostNerResults(
                testStr2,
                acTree
        );
        long end = System.currentTimeMillis();
        log.info("耗时：{}ms", end - start);
        log.info("results = {}", results);
    }

    public static List<PostNerResult> getPostNerResults(String postTitle, AhoCorasickDoubleArrayTrie<AcTreeInfo> acTree){
        if (StringUtils.hasText(postTitle) && postTitle.length() >= 2){
            List<PostNerResult> results = new ArrayList<>();
            acTree.parseText(postTitle, (begin, end, valueInfo) -> {
                PostNerResult result = new PostNerResult();
                result.setKeyWord(valueInfo.getKey());
                result.setNerType(valueInfo.getValue());
                results.add(result);
            });
            return results;
        }
        return new ArrayList<>();
    }
}
