// Expected score: 90-100
// Reason: Simple configuration class with high cohesion and clean code.
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
