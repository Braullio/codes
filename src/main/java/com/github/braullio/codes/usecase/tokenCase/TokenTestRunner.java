package com.github.braullio.codes.usecase.tokenCase;

import com.github.braullio.codes.CodesApplication;
import com.github.braullio.codes.core.token.TokenService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Runner de testes para validar:
 * 1) Uso Spring com @Autowired (ProcessoService)
 * 2) Uso estilo legado/CDI (LegacyBean)
 */
public class TokenTestRunner {

    public static void main(String[] args) throws Exception {
        System.out.println("=== INICIANDO CONTEXTO SPRING BOOT ===");

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(CodesApplication.class)
                .properties("spring.autoconfigure.exclude=" +
                        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration")
                .run(args);

        // -------- TESTE 1: Spring (@Autowired) --------
        System.out.println("\n=== TESTE 1: Spring (@Autowired) ===");

        ProcessoService processoService = ctx.getBean(ProcessoService.class);

        processoService.executar();

        // -------- TESTE 2: Legado estilo CDI --------
        System.out.println("\n=== TESTE 2: Legado estilo CDI (@Inject) ===");

        LegacyBean legacyBean = new LegacyBean();

        // Pega o mesmo bean de token que o Spring usa
        TokenService tokenService = ctx.getBean("tokenV1", TokenService.class);

        // Injeta manualmente para simular CDI em classe legado
        injetarTokenManual(legacyBean, tokenService);

        legacyBean.executar();

        System.out.println("\n=== FIM DOS TESTES ===");

        ctx.close();
    }

    private static void injetarTokenManual(LegacyBean legacyBean, TokenService tokenService) throws Exception {
        var field = LegacyBean.class.getDeclaredField("tokenService");
        field.setAccessible(true);
        field.set(legacyBean, tokenService);
    }
}