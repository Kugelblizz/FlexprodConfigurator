package at.jku.cis.iVolunteer.configurator.config.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
public class RequestMappingConfiguration extends RequestMappingHandlerAdapter {

	@Bean
	public RequestMappingHandlerAdapter requestHandler() {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		
		List<MediaType> mediaTypeList = new ArrayList<>();
		mediaTypeList.add(MediaType.APPLICATION_JSON);
		converter.setSupportedMediaTypes(mediaTypeList);
		
		ByteArrayHttpMessageConverter baConverter = new ByteArrayHttpMessageConverter();
		List<MediaType> baMediaTypeList = new ArrayList<>();
		baMediaTypeList.add(MediaType.IMAGE_GIF);
		baMediaTypeList.add(MediaType.IMAGE_JPEG);
		baMediaTypeList.add(MediaType.IMAGE_PNG);

		baConverter.setSupportedMediaTypes(baMediaTypeList);
		adapter.getMessageConverters().add(baConverter);
		adapter.getMessageConverters().add(converter);

		return adapter;
	}
}
