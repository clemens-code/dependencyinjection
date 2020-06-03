import container.LightWeightContainer;
import example.BeanToInject;
import example.BeanWithoutName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleTest {

    private LightWeightContainer container = new LightWeightContainer();

    @BeforeEach
    void testSetup()
    {
        container.setBasePackage("example");

        container.run();
    }

    @Test
    public void simpleScanTest()
    {
        assertEquals(2, container.numberOfBeans());
    }

    @Test
    public void correctBeanNameTest()
    {
        assertEquals("BeanToInject", container.getClassByBeanName("beanToInject").getSimpleName());
    }

    @Test
    public void simpleConfigAndSingletonTest()
    {
        LightWeightContainer container = new LightWeightContainer();
        container.setBasePackage("example");

        container.run();
        Class<?> beanTest = container.getBeanByClass(BeanWithoutName.class);
        Class<?> beanWithoutName = container.getBeanByClass(BeanWithoutName.class);
        assertEquals(beanTest, beanWithoutName);
    }

    @Test
    public void fieldsNamedTest()
    {
        LightWeightContainer container = new LightWeightContainer();
        container.setBasePackage("example");
        BeanWithoutName beanWithoutName = new BeanWithoutName();
        container.run();

        assertEquals("Hallo Ballo", beanWithoutName.HalloBallo());
    }
}
