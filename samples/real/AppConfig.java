public class AppConfig {
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }
    @Bean
    public TransactionManager txManager() {
        return new JpaTransactionManager();
    }
}
