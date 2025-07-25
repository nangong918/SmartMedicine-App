package com.czy.message.controller;




import com.czy.api.constant.message.MessageConstant;
import com.czy.api.converter.base.MessageConverter;
import com.czy.api.domain.dto.http.base.BaseHttpRequest;
import com.czy.api.domain.dto.http.response.ResponseEntity;
import com.czy.api.domain.entity.event.Message;
import com.czy.message.mq.sender.RabbitMqSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


/**
 * 这一块没用
 * 根本就不应该适用http
 */
@RestController
@RequestMapping(MessageConstant.WebRTC_CONTROLLER)
@Validated // 启用校验
@RequiredArgsConstructor // 自动注入@Autowired
@Api(produces = "application/json", tags = "单人通话信令推送接口 Java+Netty+WebRTC" )
public class WebrtcController {

	private final RabbitMqSender rabbitMqSender;
	private final MessageConverter messageConverter;

	@ApiOperation(httpMethod = "POST", value = "发起单人语音通话")
	@ApiImplicitParam(name = "receiverId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value = {"/voice"})
	public Mono<ResponseEntity<Void>> voice(@Valid @RequestBody BaseHttpRequest request) {

		// 内部包含：message.setType(MessageTypeTranslator.translateClean(request.getType()));
		Message message = messageConverter.baseNettyRequestToMessageAndTranslate(request);
		rabbitMqSender.push(message);

		return Mono.just(ResponseEntity.make());
	}


	@ApiOperation(httpMethod = "POST", value = "发起单人视频通话")
	@ApiImplicitParam(name = "receiverId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/video"})
	public Mono<ResponseEntity<Void>> video(@Valid @RequestBody BaseHttpRequest request) {

		// 内部包含：message.setType(MessageTypeTranslator.translateClean(request.getType()));
		Message message = messageConverter.baseNettyRequestToMessageAndTranslate(request);
		rabbitMqSender.push(message);

		return Mono.just(ResponseEntity.make());
	}

	@ApiOperation(httpMethod = "POST", value = "接受通话")
	@ApiImplicitParam(name = "receiverId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/accept"})
	public Mono<ResponseEntity<Void>> accept(@Valid @RequestBody BaseHttpRequest request) {
		// 内部包含：message.setType(MessageTypeTranslator.translateClean(request.getType()));
		Message message = messageConverter.baseNettyRequestToMessageAndTranslate(request);
		rabbitMqSender.push(message);

		return Mono.just(ResponseEntity.make());
	}

	@ApiOperation(httpMethod = "POST", value = "拒绝通话")
	@ApiImplicitParam(name = "receiverId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/reject"})
	public Mono<ResponseEntity<Void>> reject(@Valid @RequestBody BaseHttpRequest request) {

		// 内部包含：message.setType(MessageTypeTranslator.translateClean(request.getType()));
		Message message = messageConverter.baseNettyRequestToMessageAndTranslate(request);
		rabbitMqSender.push(message);

		return Mono.just(ResponseEntity.make());
	}

	@ApiOperation(httpMethod = "POST", value = "挂断通话")
	@ApiImplicitParam(name = "receiverId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/hangup"})
	public Mono<ResponseEntity<Void>> hangup(@Valid @RequestBody BaseHttpRequest request) {

		// 内部包含：message.setType(MessageTypeTranslator.translateClean(request.getType()));
		Message message = messageConverter.baseNettyRequestToMessageAndTranslate(request);
		rabbitMqSender.push(message);

		return Mono.just(ResponseEntity.make());
	}

	@ApiOperation(httpMethod = "POST", value = "取消呼叫")
	@ApiImplicitParam(name = "receiverId", value = "对方用户ID", paramType = "query",  dataTypeClass = Long.class)
	@PostMapping(value =  {"/cancel"})
	public Mono<ResponseEntity<Void>> cancel(@Valid @RequestBody BaseHttpRequest request) {

		// 内部包含：message.setType(MessageTypeTranslator.translateClean(request.getType()));
		Message message = messageConverter.baseNettyRequestToMessageAndTranslate(request);
		rabbitMqSender.push(message);

		return Mono.just(ResponseEntity.make());
	}

//	@ApiOperation(httpMethod = "POST", value = "同步IceCandidate")
//	@PostMapping(value = {"/transmit/ice"})
//	public Mono<ResponseEntity<Void>> ice(@ApiParam(hidden = true) String senderId,
//									@RequestBody WebrtcRequest request
//	) {
//
//		Message message = new Message();
//		message.setType(RequestMessageType.Call.ACTION_907);
//		message.setSenderId(senderId);
//		message.setContent(request.getContent());
//		message.setReceiverId(request.getUid());
//		pusher.push(message);
//
//		return Mono.just(ResponseEntity.make());
//	}

//	@ApiOperation(httpMethod = "POST", value = "同步offer")
//	@PostMapping(value =  {"/transmit/offer"})
//	public Mono<ResponseEntity<Void>> offer(@ApiParam(hidden = true) String senderId,
//									  @RequestBody WebrtcRequest request
//	) {
//
//		Message message = new Message();
//		message.setType(RequestMessageType.Call.ACTION_908);
//		message.setSenderId(senderId);
//		message.setContent(request.getContent());
//		message.setReceiverId(request.getUid());
//		pusher.push(message);
//
//		return Mono.just(ResponseEntity.make());
//	}

//	@ApiOperation(httpMethod = "POST", value = "同步answer")
//	@PostMapping(value =  {"/transmit/answer"})
//	public Mono<ResponseEntity<Void>> answer(@ApiParam(hidden = true) String senderId,
//									   @RequestBody WebrtcRequest request
//	) {
//
//		Message message = new Message();
//		message.setType(RequestMessageType.Call.ACTION_909);
//		message.setSenderId(senderId);
//		message.setContent(request.getContent());
//		message.setReceiverId(request.getUid());
//		pusher.push(message);
//
//		return Mono.just(ResponseEntity.make());
//	}
}
