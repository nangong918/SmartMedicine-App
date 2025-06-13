package com.czy.api.constant.netty;

/**
 * @author 13225
 * @date 2025/6/13 16:58
 */
public class MqConstants {

    /**
     * 交换机
     */
    static String EXCHANGE_KEY = ".exchange";
    public interface Exchange {
        interface R {
            String MESSAGE = ".message";
            String POST = ".post";
            String RELATIONSHIP = ".relationship";
            String OSS = ".oss";
            String LOGGING = ".logging";
        }

        String MESSAGE_EXCHANGE = "socket" + R.MESSAGE + EXCHANGE_KEY;
        String POST_EXCHANGE = "socket" + R.POST + EXCHANGE_KEY;
        String RELATIONSHIP_EXCHANGE = "socket" + R.RELATIONSHIP + EXCHANGE_KEY;
        String OSS_EXCHANGE = "socket" + R.OSS + EXCHANGE_KEY;
        String LOGGING_EXCHANGE = "socket" + R.LOGGING + EXCHANGE_KEY;
    }

    /**
     * 消息队列
     */
    static String QUEUE_KEY = ".queue";
    /// message
    public interface MessageQueue {
        interface R{
            String MESSAGE = ".message";
            String TO_USER = ".to_user";
            String TO_GROUP = ".to_group";
            String TO_ALL = ".to_all";
            String TEXT = ".text";
            String IMAGE = ".image";
            String VOICE = ".voice";
            String VIDEO = ".video";
            String FILE = ".file";
        }
        String MESSAGE_TO_USER_TEXT_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.TEXT + QUEUE_KEY;
        String MESSAGE_TO_USER_IMAGE_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.IMAGE + QUEUE_KEY;
        String MESSAGE_TO_USER_VOICE_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.VOICE + QUEUE_KEY;
        String MESSAGE_TO_USER_VIDEO_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.VIDEO + QUEUE_KEY;
        String MESSAGE_TO_USER_FILE_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.FILE + QUEUE_KEY;

        String MESSAGE_TO_GROUP_TEXT_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.TEXT + QUEUE_KEY;
        String MESSAGE_TO_GROUP_IMAGE_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.IMAGE + QUEUE_KEY;
        String MESSAGE_TO_GROUP_VOICE_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.VOICE + QUEUE_KEY;
        String MESSAGE_TO_GROUP_VIDEO_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.VIDEO + QUEUE_KEY;
        String MESSAGE_TO_GROUP_FILE_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.FILE + QUEUE_KEY;
    }

    /// post
    public interface PostQueue {
        interface R {
            String POST = ".post";
            String LIKE = R.POST + ".like";
            String COMMENT = R.POST + ".comment";
        }

        String POST_LIKE_QUEUE = "socket" + R.LIKE + QUEUE_KEY;
        String POST_COMMENT_QUEUE = "socket" + R.COMMENT + QUEUE_KEY;
    }

    /// relationship
    public interface RelationshipQueue {
        interface R {
            String RELATIONSHIP = ".relationship";
            String REQUEST = R.RELATIONSHIP + ".request";
            String HANDLE = R.RELATIONSHIP + ".handle";
            String PERMISSION = R.RELATIONSHIP + ".permission";
        }

        String RELATIONSHIP_QUEUE = "socket" + R.REQUEST + QUEUE_KEY;
        String RELATIONSHIP_HANDLE_QUEUE = "socket" + R.HANDLE + QUEUE_KEY;
        String RELATIONSHIP_PERMISSION_QUEUE = "socket" + R.PERMISSION + QUEUE_KEY;
    }

    /// oss
    public interface OssQueue {
        String OSS_QUEUE = "socket.oss.queue";
    }

    /// logging
    public interface LoggingQueue {
        interface R {
            String LOGGING = ".logging";
            String EXPLICIT = R.LOGGING + ".explicit";
            String IMPLICIT = R.LOGGING + ".implicit";
        }

        String EXPLICIT_BEHAVIOR_QUEUE = "socket" + R.EXPLICIT + QUEUE_KEY;
        String IMPLICIT_BEHAVIOR_QUEUE = "socket" + R.IMPLICIT + QUEUE_KEY;
    }

    /**
     * 死信队列配置
     */
    public interface DeadLetterQueue {
        interface R {
            String DLX = ".dlx";
            String MESSAGE = R.DLX + ".message";
            String POST = R.DLX + ".post";
            String RELATION = R.DLX + ".relation";
            String OSS = R.DLX + ".oss";
        }

        String DLX_EXCHANGE = "socket" + R.DLX + EXCHANGE_KEY;
        String MESSAGE_DLX_QUEUE = "socket" + R.MESSAGE + QUEUE_KEY;
        String POST_DLX_QUEUE = "socket" + R.POST + QUEUE_KEY;
        String RELATION_DLX_QUEUE = "socket" + R.RELATION + QUEUE_KEY;
        String OSS_DLX_QUEUE = "socket" + R.OSS + QUEUE_KEY;
    }
}
