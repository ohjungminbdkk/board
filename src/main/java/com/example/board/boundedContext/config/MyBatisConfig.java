package com.example.board.boundedContext.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.example.board.boundedContext.question")
public class MyBatisConfig {
	/*
	 * @Bean public SqlSessionFactory sqlSessionFactory(DataSource dataSource)
	 * throws Exception { SqlSessionFactoryBean sessionFactory = new
	 * SqlSessionFactoryBean(); sessionFactory.setDataSource(dataSource); return
	 * sessionFactory.getObject(); }
	 * 
	 * 
	 */
	@Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // 🔥 MyBatis XML Mapper 설정 추가
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/*.xml")
        );

        return sessionFactory.getObject();
    }
}
