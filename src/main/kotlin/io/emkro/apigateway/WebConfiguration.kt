package io.emkro.apigateway

import org.springframework.context.annotation.Configuration
import java.util.Locale

@Configuration
class WebConfiguration {

    init {
        Locale.setDefault(Locale.ENGLISH)
    }
}
