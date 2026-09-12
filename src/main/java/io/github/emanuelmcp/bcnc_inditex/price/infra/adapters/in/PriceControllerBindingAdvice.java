package io.github.emanuelmcp.bcnc_inditex.price.infra.adapters.in;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice(assignableTypes = PriceController.class)
public class PriceControllerBindingAdvice {
    @InitBinder
    public void registerApplicationDateFormatter(WebDataBinder binder) {
        binder.addCustomFormatter(new ApplicationDateFormatter());
    }
}
