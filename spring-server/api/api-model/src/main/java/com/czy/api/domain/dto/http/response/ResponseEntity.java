package com.czy.api.domain.dto.http.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseEntity<T> {
	private int code = 200;
	private String message;
	private T data;
	private String token;
	private Long timestamp;

	public static ResponseEntity<Void> make(){
		return new ResponseEntity<>();
	}

	public static ResponseEntity<Void> make(int code){
		return make(code,null);
	}

	public static <T> ResponseEntity<T> make(int code,String message){
		ResponseEntity<T> result = new ResponseEntity<>();
		result.setCode(code);
		result.setMessage(message);
		return result;
	}


	public static <Q> ResponseEntity<Q> ok(Q data){
		ResponseEntity<Q> result = new ResponseEntity<>();
		result.setData(data);
		return result;
	}

}
