package com.kakaopay.throwmoney.common.exception;

import org.springframework.http.HttpStatus;

/**
 * The Class AbstractApiException. 
 *
 * 400 Bad Request - field validation 실패시, Body가 구문 분석하지 않는 경우와 같이 요청이 잘못됨.
 * 401 Unauthorized - API 인증,인가 실패 , 인증 정보가 제공되지 않거나 유효하지 않은 경우. 실제로는 Unauthenticated 의미
 * 403 Forbidden 인증이 성공했지만 인증 된 사용자가 자원에 액세스 할 수 없는 경우, 실제로는 Unauthorized 의미
 * 404 Not found 존재하지 않는 리소스가 요청 된 경우
 * 405 Method Not Allowed	인증 된 사용자에게 허용되지 않는 HTTP 메소드가 요청 될 때
 * 409 Conflict 충돌, 이미 생성하고자 하는 리소스가 존재함
 * 410 Gone 은 요청된 리소스를 더 이상 사용할 수 없는 경우에 표시됩니다. 404의 대체로 사용, 엔드포인트의 자원을 더 이상 사용할 수 없음. 서버는 요청한 리소스가 영구적으로 삭제되었을 때 이 응답을 표시한다.
 * 415 Unsupported Media Type	요청의 일부로 잘못된 콘텐츠 유형이 제공된 경우
 * 422 Unprocessable Entity	 유효성 검사 오류에 사용.
 * 429 Too Many Requests	속도 제한으로 인해 요청이 거부 된 경우.
 * 500 Internal Server Error	서버에서 요청 처리 중 오류.
 * 503 ServiceUnavailable 은 일반적으로 로드가 많거나 유지 관리 문제 때문에 일시적으로 서버를 사용할 수 없는 경우에 표시됩니다.
 *
 * http://www.coolcheck.co.kr/upload/http_scode.asp
 */
public class ApiException extends AbstractApiException
{
	private static final long serialVersionUID = -7823800670506884689L;

	public ApiException(String message)
	{
		super(message, HttpStatus.INTERNAL_SERVER_ERROR, null);
	}
	
	public ApiException(String message, Throwable cause)
	{
		super(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
	}
	
	public ApiException(String message, HttpStatus httpStatus)
	{
		super(message, httpStatus, null);
	}
	
	public ApiException(String message, HttpStatus httpStatus, int code)
	{
		super(message, httpStatus, code);
	}

	public ApiException(String message, HttpStatus httpStatus, Throwable cause)
	{
		super(message, httpStatus, cause);
	}

	public ApiException(String message, int code)
	{
		super(message, code, null);
	}
	
	public ApiException(String message, int code, Throwable cause)
	{
		super(message, code, cause);
	}
	
	public ApiException(String message, HttpStatus httpStatus, int code, Throwable cause)
	{
		super(message, httpStatus, code, cause);
	}
}
