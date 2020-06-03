package example;

import annotations.Beans;
import annotations.Named;

@Beans
@Named(named = "beanToInject")
public class BeanToInject {

    public BeanToInject()
    {
    }

    String hello()
    {
        return "Hello";
    }
}
