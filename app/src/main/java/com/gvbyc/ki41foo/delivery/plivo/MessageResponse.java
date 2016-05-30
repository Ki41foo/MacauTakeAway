package com.gvbyc.ki41foo.delivery.plivo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageResponse {
	@SerializedName("server_code")
	public Integer serverCode ;

    public String message ;
    
    @SerializedName("message_uuid")
    public List<String> messageUuids ;
    
    public String error ;
    
    @SerializedName("api_id")
    public String apiId ;
    
    public MessageResponse() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public String toString() {
		return "MessageResponse [serverCode=" + serverCode + ", message="
				+ message + ", messageUuids=" + messageUuids + ", error="
				+ error + ", apiId=" + apiId + "]";
	}
}
