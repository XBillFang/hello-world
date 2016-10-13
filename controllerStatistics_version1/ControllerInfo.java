package com.tasly.anguo.storefront.controllers;

public class ControllerInfo
{
	private String controllerName;
	private String description;
	private String requestMappingOfClass;
	private String methodName;
	private String requestMappingOfMethod;
	private String fullName;

	public String getControllerName()
	{
		return controllerName;
	}
	public void setControllerName(String controllerName)
	{
		this.controllerName = controllerName;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getRequestMappingOfClass()
	{
		return requestMappingOfClass;
	}
	public void setRequestMappingOfClass(String requestMappingOfClass)
	{
		this.requestMappingOfClass = requestMappingOfClass;
	}
	public String getMethodName()
	{
		return methodName;
	}
	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}
	public String getRequestMappingOfMethod()
	{
		return requestMappingOfMethod;
	}
	public void setRequestMappingOfMethod(String requestMappingOfMethod)
	{
		this.requestMappingOfMethod = requestMappingOfMethod;
	}
	public String getFullName()
	{
		return fullName;
	}
	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}
}
