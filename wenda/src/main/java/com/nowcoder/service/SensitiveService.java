package com.nowcoder.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤
 *
 */
@Service
public class SensitiveService implements InitializingBean {

	//日志
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    
    //默认敏感词替换符
    private static final String DEFAULT_REPLACEMENT = "**敏感词**";


    //敏感词组成的前缀树
    //这是一个类！！！！！！
    private class TrieNode {

        //true 关键词的终结 ； false 继续
        private boolean end = false;

        /**
         * key下一个字符，value是对应的节点
         */
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        /**
         * 向指定位置添加节点树
         */
        void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        /**
         * 获取下个节点
         */
        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        //判断是否是敏感词结尾节点
        boolean isKeywordEnd() {
            return end;
        }

        //设置成敏感词结尾节点
        void setKeywordEnd(boolean end) {
            this.end = end;
        }

        //获得节点总数
        public int getSubNodeCount() {
            return subNodes.size();
        }


    }


    /**
     * 根节点
     */
    private TrieNode rootNode = new TrieNode();


    /**
     * 判断是否是一个符号，是符号返回true，为了让这种中间有空格的“色   情”也能被过滤掉
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }


    /**
     * 过滤敏感词的逻辑函数！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     */
    public String filter(String text) {
    	//text是要过滤的内容
    	//如果要过滤的内容是空，则直接跳出
        if (StringUtils.isBlank(text)) {
            return text;
        }
        
        //定义替换词
        String replacement = DEFAULT_REPLACEMENT;
        
        //定义最终过滤结果
        StringBuilder result = new StringBuilder();

        
        TrieNode tempNode = rootNode;	// 树根，视频中蓝色指针1
        int begin = 0; 					// 回滚数，视频中红色指针2
        int position = 0; 				// 当前比较的位置，视频中指针3

        //开始进行过滤
        while (position < text.length()) {
            char c = text.charAt(position);
            // 空格或者符号直接跳过
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            // 当前位置的匹配结束
            if (tempNode == null) {
                // 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                // 回到树初始节点
                tempNode = rootNode;
      /********************* 上面是未发现敏感词的处理，下面两个else是发现铭感词的处理 **************/
            } else if (tempNode.isKeywordEnd()) {
                // 发现了完整敏感词，比如发现了“色情” 从begin到position的位置用replacement替换掉
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
            	//发现了部分敏感词，比如“色情”中的“色”
                ++position;
            }
        }

        //最后一个字符也要添加到过滤结果里
        result.append(text.substring(begin));

        return result.toString();
    }

    //往前缀树中添加敏感词，作为过滤参照
    private void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;
        // 循环每个字节
        for (int i = 0; i < lineTxt.length(); ++i) {
            Character c = lineTxt.charAt(i);
            // 过滤空格
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);

            if (node == null) { // 没初始化
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                // 关键词结束， 设置结束标志
                tempNode.setKeywordEnd(true);
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();

        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    public static void main(String[] argv) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("好色");
        System.out.print(s.filter("你好X色**情XX"));
    }
}
