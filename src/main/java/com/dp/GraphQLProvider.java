package com.dp;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Component
public class GraphQLProvider {
	
	@Autowired
    GraphQLDataFetchers graphQLDataFetchers;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	private GraphQL graphQL;
	
	@Bean
    public GraphQL graphQL() { 
        return graphQL;
    }
	
	@PostConstruct
	public void init() throws Exception {
		GraphQLSchema graphQLSchema = buildSchema();
		this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
	}

	@Bean
	GraphQLSchema buildSchema() throws IOException {
		Resource resource = resourceLoader.getResource("classpath:graphql/schema.graphqls");
	    InputStream inputStream = resource.getInputStream();
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(inputStream);
		RuntimeWiring runtimeWiring = buildWiring();
		SchemaGenerator schemaGenerator  = new SchemaGenerator();
		return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
	}

	private RuntimeWiring buildWiring() {
		// TODO Auto-generated method stub
		return RuntimeWiring.newRuntimeWiring()
				.type(newTypeWiring("Query")
                        .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher()))
                .type(newTypeWiring("Book")
                        .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher()))
                .build();
	}
	

}
