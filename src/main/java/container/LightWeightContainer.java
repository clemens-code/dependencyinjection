package container;


import annotations.Beans;
import annotations.Inject;
import annotations.Named;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class LightWeightContainer {

    private static final Logger LOG = LoggerFactory.getLogger(LightWeightContainer.class);

    private String basePackage;

    private static Map<String, Class<?>> instantiatedClasses;

    public void addBean(String name, Class<?> clazz)
    {
        singletonClass(name, clazz);
    }

    private static synchronized void singletonClass(String name, Class<?> clazz)
    {
        if(instantiatedClasses == null)
        {
            instantiatedClasses = new HashMap<>();
        }

        if(instantiatedClasses.containsKey(name))
        {
            LOG.error("Unexpected Error caused by {}", name);
            throw new RuntimeException("The Bean with the name"+ name +" has already been declared!");
        }else
        {
            try {
                LOG.debug("initiation of "+clazz.getSimpleName());
                clazz.getDeclaredConstructor().newInstance();
            }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e)
            {
                throw new RuntimeException("Error during initiation of "+ clazz.getSimpleName() +" for further details see the Log!");
            }
            LOG.debug("Registration of Bean {} successful", name);
            instantiatedClasses.put(name, clazz);
        }
    }

    /**
     * Starts the process of initialisation of the beans
     */
    public void run() {
        LOG.debug("Start of Bean registration");
        Objects.requireNonNull(basePackage, "The basePackage Attribute need to be set!");
        LOG.debug("Scanning the package{}", basePackage);
        Reflections reflections = new Reflections(getBasePackage());
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Beans.class);

        for(Class<?> clazz : annotatedClasses)
        {
            addBean((clazz.isAnnotationPresent(Named.class) ? clazz.getAnnotation(Named.class).named() : basePackage+"."+clazz.getSimpleName()), clazz);
        }
        //addInjectedFields();
        //Auskommentiert, da es zu Fehlern bei lauffähigen Tests führt.
    }

    public Class<?> getBeanByClass(Class<?> clazz)
    {
        LOG.debug("Searching bean for class {}", clazz.getSimpleName());
        for(Map.Entry<String, Class<?>> entry : instantiatedClasses.entrySet())
        {
            if(entry.getValue().equals(clazz))
                return clazz;
        }
        return null;
    }

    private void addInjectedFields() {
        for(Map.Entry<String, Class<?>> entry : instantiatedClasses.entrySet())
        {
            for (Field field : entry.getValue().getDeclaredFields()) {
                if (field.getAnnotation(Inject.class) != null)
                {
                    String named = field.getAnnotation(Named.class).named();
                    if (named != null )
                    {
                        if(instantiatedClasses.containsKey(named))
                        {
                            try
                            {
                                field.set(instantiatedClasses.get(named).getClass(), instantiatedClasses.get(named).getClass());
                            }catch (IllegalAccessException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                        else
                        {
                            throw new RuntimeException("There were no Bean found for the name "+named);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @return amount of Beans in a package
     */
    public int numberOfBeans()
    {
        LOG.debug("Counting beans in {}", basePackage);
        Reflections reflections = new Reflections(getBasePackage());
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Beans.class);
        return annotatedClasses.size();
    }

    public Class getClassByBeanName(String name)
    {
        return instantiatedClasses.get(name);
    }

    /**
     * Only changes the basePackage ones to the beginning of the Runtime.
     * @param newPackage
     */
    public void setBasePackage(String newPackage)
    {
        if(basePackage == null)
        {
            basePackage = new String();
        }

        if(basePackage.isEmpty())
        {
            basePackage = newPackage;
        }
    }

    private String getBasePackage()
    {
        return basePackage;
    }
}
