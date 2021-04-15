package main.config;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import main.request.RequestLoginDto;
import main.response.ResponseLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import springfox.documentation.builders.ApiDescriptionBuilder;
import springfox.documentation.builders.ApiListingBuilder;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Operation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;
import springfox.documentation.spring.web.scanners.ApiDescriptionReader;
import springfox.documentation.spring.web.scanners.ApiListingScanner;
import springfox.documentation.spring.web.scanners.ApiListingScanningContext;
import springfox.documentation.spring.web.scanners.ApiModelReader;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

  @Autowired
  private TypeResolver typeResolver;

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()
        .additionalModels(
            typeResolver.resolve(RequestLoginDto.class),
            typeResolver.resolve(ResponseLoginDto.class),
            typeResolver.resolve(RequestLoginDto.class)
        );
  }

  @Primary
  @Bean
  public ApiListingScanner addExtraOperations(ApiDescriptionReader apiDescriptionReader,
      ApiModelReader apiModelReader, DocumentationPluginsManager pluginsManager) {
    return new FormLoginOperations(apiDescriptionReader, apiModelReader, pluginsManager);
  }
}

class FormLoginOperations extends ApiListingScanner {

  @Autowired
  private TypeResolver typeResolver;

  @Autowired
  public FormLoginOperations(ApiDescriptionReader apiDescriptionReader,
      ApiModelReader apiModelReader, DocumentationPluginsManager pluginsManager) {
    super(apiDescriptionReader, apiModelReader, pluginsManager);
  }

  @Override
  public Multimap<String, ApiListing> scan(ApiListingScanningContext context) {
    final Multimap<String, ApiListing> def = super.scan(context);

    final List<ApiDescription> apis = new LinkedList<>();

    final List<Operation> operations = new ArrayList<>();
    operations.add(new OperationBuilder(new CachingOperationNameGenerator())
        .method(HttpMethod.POST)
        .uniqueId("login")
        .parameters(singletonList(new ParameterBuilder()
            .name("Login Parameter")
            .required(true)
            .description("Login form")
            .parameterType("body")
            .parameterAccess("access")
            .required(true)
            .type(new TypeResolver().resolve(RequestLoginDto.class))
            .modelRef(new ModelRef("RequestLoginDto"))
            .build()))
        .responseMessages(singleton(
            new ResponseMessageBuilder()
                .code(200)
                .message("You are logged in")
                .responseModel(new ModelRef("ResponseLoginDto"))
                .build()
        ))
        .summary("Log in")
        .notes("Here you can log in")
        .build());

    apis.add(
        new ApiDescriptionBuilder(new Ordering<>() {
          @Override
          public int compare(@Nullable Operation operation, @Nullable Operation t1) {
            return 0;
          }
        })
            .path("/api/auth/login")
            .description("Login")
            .operations(operations)
            .build()
    );

    def.put("AUTH",
        new ApiListingBuilder(context.getDocumentationContext().getApiDescriptionOrdering())
            .apis(apis)
            .position(1)
            .description("Custom auth")
            .build()
    );

    return def;
  }
}