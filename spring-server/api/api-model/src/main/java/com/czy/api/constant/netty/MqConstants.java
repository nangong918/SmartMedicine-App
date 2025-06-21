package com.czy.api.constant.netty;

/**
 * @author 13225
 * @date 2025/6/13 16:58
 * 1. 业务消息都是可靠消息，都需要确认发送和应答机制，持久化。
 * 2. 需要限制消息队列长度，避免消息堆积，若发生消息堆积则加入对应死信队列，在运维层面调整Mq参数。
 * 3. 消息队列需要设定消息的TTL，避免消息堆积。同样进入死信队列之后要告警，运维检查系统的出现问题的原因。
 * 4. 进入死信队列的消息需要进行持久化，并设计重试机制。
 * 5. 需要速度并且需要可靠性的消息使用普通消息队列，比如说Message消息。
 *    其他的消息使用惰性队列，让mq尽可能少地占用内存。
 *    需要速度，数据量很大，并且无需要可靠性则使用kafka。例如logging日志消息。
 *
 * 我的消息队列的目的只是为了服务解耦，发送到对应的微服务就可以，根本没必要这么多消息队列，消息在微服务内部分类处理就好了
 */
public interface MqConstants {
    /*
        基本组件：
            交换机
            队列
            死信
     */
    String EXCHANGE = ".exchange";
    String QUEUE = ".queue";
//    String DLX = ".dlx";
    /*
        1. 消息需要从netty-socket发送给service
        2. netty-socket也需要接收各个service发送的消息
     */
    String TO_SOCKET = ".to_socket";
    String TO_SERVICE = ".to_service";

    /**
     * 交换机
     */
    interface Exchange {
//        interface R {
//            String MESSAGE = ".message";
//            String POST = ".post";
//            String RELATIONSHIP = ".relationship";
//            String OSS = ".oss";
//            // kafka
//            String LOGGING = ".logging";
//        }

        String MESSAGE_EXCHANGE = MessageQueue.ID + EXCHANGE;
        String POST_EXCHANGE = PostQueue.ID + EXCHANGE;
        String RELATIONSHIP_EXCHANGE = RelationshipQueue.ID + EXCHANGE;
        String OSS_EXCHANGE = OssQueue.ID + EXCHANGE;
        String LOGGING_EXCHANGE = LoggingQueue.ID + EXCHANGE;
        // 死信是小概率事件，所以死信单独设计交换机
        String DEAD_LETTER_EXCHANGE = DeadLetterQueue.ID + EXCHANGE;
    }



    /**
     * 消息队列
     */
    String QUEUE_KEY = ".queue";
    /// message
    interface MessageQueue {
        String ID = ".message";
        String MESSAGE_TO_SOCKET_QUEUE = Routing.TO_SOCKET_ROUTING + QUEUE;
        String MESSAGE_TO_SERVICE_QUEUE = Routing.TO_SERVICE_ROUTING + QUEUE;


        Long message_ttl = 10 * 60 * 3000L;
        String message_ttl_str = "1800000";
        Integer message_max_length = 1000_000;

        interface Routing{
            String TO_SOCKET_ROUTING = TO_SOCKET + ID;
            String TO_SERVICE_ROUTING = TO_SERVICE + ID;
        }
//        interface R{
//            String MESSAGE = ".message";
//            String TO_USER = ".to_user";
//            String TO_GROUP = ".to_group";
//            String TO_ALL = ".to_all";
//            String TEXT = ".text";
//            String IMAGE = ".image";
//            String VOICE = ".voice";
//            String VIDEO = ".video";
//            String FILE = ".file";
//        }

//        String MESSAGE_TO_USER_TEXT_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.TEXT + QUEUE_KEY;
//        String MESSAGE_TO_USER_IMAGE_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.IMAGE + QUEUE_KEY;
//        String MESSAGE_TO_USER_VOICE_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.VOICE + QUEUE_KEY;
//        String MESSAGE_TO_USER_VIDEO_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.VIDEO + QUEUE_KEY;
//        String MESSAGE_TO_USER_FILE_QUEUE = "socket" + R.MESSAGE + R.TO_USER + R.FILE + QUEUE_KEY;
//
//        String MESSAGE_TO_GROUP_TEXT_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.TEXT + QUEUE_KEY;
//        String MESSAGE_TO_GROUP_IMAGE_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.IMAGE + QUEUE_KEY;
//        String MESSAGE_TO_GROUP_VOICE_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.VOICE + QUEUE_KEY;
//        String MESSAGE_TO_GROUP_VIDEO_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.VIDEO + QUEUE_KEY;
//        String MESSAGE_TO_GROUP_FILE_QUEUE = "socket" + R.MESSAGE + R.TO_GROUP + R.FILE + QUEUE_KEY;
    }

    /// post
    interface PostQueue {
        String ID = ".post";
        Long message_ttl = 10 * 60 * 1000L;
        Integer message_max_length = 1000_000;

        interface Routing {
            String TO_SOCKET_ROUTING = TO_SOCKET + ID;
            String TO_SERVICE_ROUTING = TO_SERVICE + ID;
        }

        String POST_TO_SOCKET_QUEUE = Routing.TO_SOCKET_ROUTING + QUEUE;
        String POST_TO_SERVICE_QUEUE = Routing.TO_SERVICE_ROUTING + QUEUE;
//        interface R {
//            String POST = ".post";
//            String LIKE = R.POST + ".like";
//            String COMMENT = R.POST + ".comment";
//        }
//
//        String POST_LIKE_QUEUE = "socket" + R.LIKE + QUEUE_KEY;
//        String POST_COMMENT_QUEUE = "socket" + R.COMMENT + QUEUE_KEY;
    }

    /// relationship
    interface RelationshipQueue {
        String ID = ".relationship";
        Long message_ttl = 10 * 60 * 1000L;
        Object message_max_length = 1000_000;

        interface Routing {
            String TO_SOCKET_ROUTING = TO_SOCKET + ID;
            String TO_SERVICE_ROUTING = TO_SERVICE + ID;
        }

        String RELATIONSHIP_TO_SOCKET_QUEUE = Routing.TO_SOCKET_ROUTING + QUEUE;
        String RELATIONSHIP_TO_SERVICE_QUEUE = Routing.TO_SERVICE_ROUTING + QUEUE;
//        interface R {
//            String RELATIONSHIP = ".relationship";
//            String REQUEST = R.RELATIONSHIP + ".request";
//            String HANDLE = R.RELATIONSHIP + ".handle";
//            String PERMISSION = R.RELATIONSHIP + ".permission";
//        }
//        // 好友申请消息 -> netty -(request.to_service)> service(service做存储处理) -(request.to_socket)> netty -> 接收方
//        // 好友处理消息 -> netty -(handle.to_service)> service(service做状态更改处理) -(handle.to_socket)> netty -> 接收方
//        // 权限更改消息 -> netty -(permission.to_service)> service(service做权限处理) -(permission.to_socket)> netty -> 接收方
//        String RELATIONSHIP_QUEUE = "socket" + R.REQUEST + QUEUE_KEY;
//        String RELATIONSHIP_HANDLE_QUEUE = "socket" + R.HANDLE + QUEUE_KEY;
//        String RELATIONSHIP_PERMISSION_QUEUE = "socket" + R.PERMISSION + QUEUE_KEY;
    }

    /// oss
    interface OssQueue {
//        String OSS_QUEUE = "socket.oss.queue";
        String ID = ".oss";
        Long message_ttl = 10 * 60 * 1000L;
        Object message_max_length = 1000_000;
        interface Routing {
            String TO_SOCKET_ROUTING = TO_SOCKET + ID;
            String TO_SERVICE_ROUTING = TO_SERVICE + ID;
        }
        String OSS_TO_SOCKET_QUEUE = Routing.TO_SOCKET_ROUTING + QUEUE;
        String OSS_TO_SERVICE_QUEUE = Routing.TO_SERVICE_ROUTING + QUEUE;
    }

    /// logging
    interface LoggingQueue {
//        interface R {
//            String LOGGING = ".logging";
//            String EXPLICIT = R.LOGGING + ".explicit";
//            String IMPLICIT = R.LOGGING + ".implicit";
//        }
//
//        String EXPLICIT_BEHAVIOR_QUEUE = "socket" + R.EXPLICIT + QUEUE_KEY;
//        String IMPLICIT_BEHAVIOR_QUEUE = "socket" + R.IMPLICIT + QUEUE_KEY;
        String ID = ".logging";
        String LOGGING_TO_SOCKET_QUEUE = TO_SOCKET + ID + QUEUE;
        String LOGGING_TO_SERVICE_QUEUE = TO_SERVICE + ID + QUEUE;
    }

    /**
     * 死信队列配置
     */
    interface DeadLetterQueue {
        String ID = ".dead_letter";
        interface Routing{
            String MESSAGE_DEAD_LETTER_ROUTING = MessageQueue.ID + ID;
            String POST_DEAD_LETTER_ROUTING = PostQueue.ID + ID;
            String RELATIONSHIP_DEAD_LETTER_ROUTING = RelationshipQueue.ID + ID;
            String OSS_DEAD_LETTER_ROUTING = OssQueue.ID + ID;
            String ALL_DEAD_LETTER_ROUTING = "#." + ID;
        }
        String MESSAGE_DEAD_LETTER_QUEUE = Routing.MESSAGE_DEAD_LETTER_ROUTING + QUEUE;
        String POST_DEAD_LETTER_QUEUE = Routing.POST_DEAD_LETTER_ROUTING + QUEUE;
        String RELATIONSHIP_DEAD_LETTER_QUEUE = Routing.RELATIONSHIP_DEAD_LETTER_ROUTING + QUEUE;
        String OSS_DEAD_LETTER_QUEUE = Routing.OSS_DEAD_LETTER_ROUTING + QUEUE;
        String ALL_DEAD_LETTER_QUEUE = Routing.ALL_DEAD_LETTER_ROUTING + QUEUE;
        // logging没有死信队列

//        interface R {
//            String MESSAGE = DLX + ".message";
//            String POST = DLX + ".post";
//            String RELATION = DLX + ".relation";
//            String OSS = DLX + ".oss";
//        }
//
//        String DLX_EXCHANGE = "socket" + DLX + EXCHANGE;
//        String MESSAGE_DLX_QUEUE = "socket" + R.MESSAGE + QUEUE_KEY;
//        String POST_DLX_QUEUE = "socket" + R.POST + QUEUE_KEY;
//        String RELATION_DLX_QUEUE = "socket" + R.RELATION + QUEUE_KEY;
//        String OSS_DLX_QUEUE = "socket" + R.OSS + QUEUE_KEY;

        String TO_SERVER = "to-server";
    }
}
