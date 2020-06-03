package example;

import annotations.Beans;
import annotations.Inject;
import annotations.Named;

@Beans
public class BeanWithoutName {

    @Inject
    @Named(named = "beanToInject")
    public BeanToInject b;

    public BeanWithoutName(){}

    public String HalloBallo() {
        return(b.hello()+" Ballo!");
    }
}
