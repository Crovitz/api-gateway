package com.finecontract.apigateway.configuration

import org.springframework.context.annotation.Configuration
import java.util.Locale

@Configuration
class WebConfiguration {

    init {
        Locale.setDefault(Locale.ENGLISH)
    }
}
