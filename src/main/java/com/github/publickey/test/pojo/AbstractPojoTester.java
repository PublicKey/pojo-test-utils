package com.github.publickey.test.pojo;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;

// http://danhaywood.com/2010/06/11/pojo-properties-auto-testing/

public abstract class AbstractPojoTester {
	private final Logger logger = Logger.getLogger(getClass().getName());

	private Map<Class<?>, Supplier<? extends Object>> testValues = new HashMap<Class<?>, Supplier<? extends Object>>();

	protected <T> void putTestValue(Class<T> propertyType, T testValue) {
		testValues.put(propertyType, () -> testValue);
	}
	
	protected <T> void putTestValueSupplier(Class<T> propertyType, Supplier<T> testValue) {
		testValues.put(propertyType, testValue);
	}

	@Before
	public void setUpTestValues() throws Exception {
		// add in further test values here or extending class' setUp
		putTestValue(String.class, "foo");
		putTestValue(int.class, 123);
		putTestValue(Integer.class, 123);
		putTestValue(long.class, 123L);
		putTestValue(Long.class, 123L);
		putTestValue(double.class, 123.0);
		putTestValue(Double.class, 123.0);
		putTestValue(boolean.class, true);
		putTestValue(Boolean.class, true);
		putTestValue(BigInteger.class, BigInteger.valueOf(123));
		putTestValue(BigDecimal.class, BigDecimal.valueOf(123.4));
		putTestValueSupplier(Date.class, () -> new Date());
		putTestValue(Throwable.class, new Throwable());
		putTestValue(Exception.class, new Exception());
		putTestValue(RuntimeException.class, new RuntimeException());
		putTestValueSupplier(UUID.class, () -> UUID.randomUUID());
	}

	/**
	 * Call from subclass
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 * @throws IntrospectionException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	protected <T> T testPojo(Class<T> pojoClass, String... methods) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IntrospectionException, NoSuchFieldException, SecurityException {
		try {
			T pojo = pojoClass.cast(createObject(pojoClass));
			testPojo(pojoClass, pojo, methods);
			return pojo;
		} catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException)ex.getTargetException();
			}
			throw ex;
		}
	}

	/**
	 * Invoke all constructors and specified methods
	 * @param pojoClass
	 * @param invokeMethods
	 * @throws RuntimeException
	 */
	@SuppressWarnings("unchecked")
	protected <T> T[] testPojoAllConstructors(Class<T> pojoClass, String... methods) throws RuntimeException {
		
		try {
			boolean foundOne = false;
			
			if (pojoClass.isEnum()) {
				Object[] enums = pojoClass.getEnumConstants();
				for (Object enumConstant : enums) {
					testEnum(pojoClass, enumConstant, methods);
				}

				try {
					Method method = pojoClass.getMethod("values");
					Object[] actual = (Object[])method.invoke(null);
					Assert.assertArrayEquals("Enum " + pojoClass + "." + enums + " does not match " + actual, enums, actual);
				} catch (NoSuchMethodException ex) {
					logger.warning("No values method found in the Enum " + pojoClass);
				}
				
				return (T[])enums;
			} else {
				List<Object> generatedObjects = new ArrayList<>();
				
				for (Constructor<?> constructor : pojoClass.getConstructors()) {
					try {
						Object obj = createInstance(constructor);
						generatedObjects.add(obj);

						testPojo(pojoClass, obj, methods);
	
						foundOne = true;
					} catch (NoSuchMethodException ex2) {
						// skipping this constructor
					}
				}
				
				if (!foundOne) {
					throw new InstantiationException("Unable to create any instances of " + pojoClass.getSimpleName());
				}
				
				return (T[])generatedObjects.toArray();
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void testEnum(Class<?> pojoClass, Object enumConstant, String... methods) throws Exception {
		testPojo(pojoClass, enumConstant, methods);
		try {
			Method method = pojoClass.getMethod("valueOf", String.class);
	
			String enumName = ((Enum<?>)enumConstant).name();
	
			Object result = method.invoke(enumConstant, enumName);
			Assert.assertEquals(enumConstant, result);
		} catch (NoSuchMethodException ex) {
			logger.warning("No valueOf method found in the Enum " + pojoClass);
		}
	}
	
	/**
	 * Run automated accessors, equals tests, call from subclass, as well as try to invoke all specified methods
	 * 
	 * @param pojoClass
	 * @param pojoInstance
	 * @param invokeMethods
	 * @throws IntrospectionException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 * @throws Exception
	 */
	protected void testPojo(Class<?> pojoClass, Object pojoInstance, String... methods) throws IntrospectionException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		BeanInfo pojoInfo = Introspector.getBeanInfo(pojoClass, pojoClass.getSuperclass());
		for (PropertyDescriptor propertyDescriptor : pojoInfo.getPropertyDescriptors()) {
			testProperty(pojoInstance, propertyDescriptor);
		}
		
		List<String> patternsLeft = new ArrayList<String>();
		patternsLeft.addAll(Arrays.asList(methods));
		
		if (methods != null && methods.length > 0) {
			for (MethodDescriptor methodDescriptor : pojoInfo.getMethodDescriptors()) {
				for (String methodName : methods) {
					if (Pattern.matches(methodName, methodDescriptor.getName())) {
						testMethod(pojoInstance, methodDescriptor);
						patternsLeft.remove(methodName);
						break;
					}
				}
				
			}
		}
		
		Assert.assertTrue("Instance is null", pojoInstance != null);
		Assert.assertTrue("self equals is not valid", pojoInstance.equals(pojoInstance));
		Assert.assertEquals("Hash code is not valid", pojoInstance.hashCode(), pojoInstance.hashCode());
		Assert.assertFalse(pojoInstance.equals("classCastSafe"));
		Assert.assertFalse("equals is not valid", pojoInstance.equals(null));
		Assert.assertNotNull("toString returned null", pojoInstance.toString());
		Assert.assertTrue("Not all methods invoked: " + patternsLeft, patternsLeft.isEmpty());
	}

	private Object createObject(Class<?> propertyType) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (propertyType.isEnum()) {
			Object[] enums = propertyType.getEnumConstants();
			if (enums.length > 0) {
				return enums[0];
			} else {
				throw new NoSuchMethodException("Enum type " + propertyType + " has no constants");
			}
		}
		
		try {
			// first try to use default constructor
			Constructor<?> constructor = propertyType.getConstructor();
			return constructor.newInstance();
		} catch (NoSuchMethodException ex) {
			// try to find constructor that we can invoke

			
			for (Constructor<?> constructor : propertyType.getConstructors()) {
				try {
					return createInstance(constructor);
				} catch (NoSuchMethodException ex2) {
					// skipping this constructor
				}
			}

			// see if a static method would create an instance of this
			for (Method method : propertyType.getDeclaredMethods()) {
				if (Modifier.isStatic(method.getModifiers()) && propertyType.isAssignableFrom( method.getReturnType() )) {
					try {
						return createInstance(method);
					}  catch (NoSuchMethodException ex2) {
						 // skipping this method
					}
				}
			}
			// none of the constructors matched
			throw new NoSuchMethodException("Unable to find any supported constructor for " + propertyType.getSimpleName());
		}
	}

	private Object createInstance(Constructor<?> constructor) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Type[] parameterTypes = constructor.getGenericParameterTypes();
		
		Object[] arguments = lookupOrCreateArguments(parameterTypes);
		
		// no exception was thrown, so let's try to create the instance
		return constructor.newInstance(arguments);
	}

	private Object createInstance(Method constructorMethod) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Type[] parameterTypes = constructorMethod.getGenericParameterTypes();
		
		Object[] arguments = lookupOrCreateArguments(parameterTypes);
		
		// no exception was thrown, so let's try to create the instance
		return constructorMethod.invoke(null, arguments);
	}
	
	@SuppressWarnings("unchecked")
	private Collection<Object> createCollectionFrom(Class<?> argumentClass) throws InstantiationException, IllegalAccessException {
		if (!Modifier.isAbstract(argumentClass.getModifiers())) {
			return (Collection<Object>)argumentClass.newInstance();
		} else if (List.class.isAssignableFrom(argumentClass)) {
			return new ArrayList<>();
		} else if (Set.class.isAssignableFrom(argumentClass)) {
			return new HashSet<>();
		} else {
			throw new InstantiationException("Unable to instantiate collection " + argumentClass);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> createMapFrom(Class<?> argumentClass) throws InstantiationException, IllegalAccessException {
		if (!Modifier.isAbstract(argumentClass.getModifiers())) {
			return (Map<Object, Object>)argumentClass.newInstance();
		} else if (Map.class.isAssignableFrom(argumentClass)) {
			return new HashMap<>(); 
		} else {
			throw new InstantiationException("Unable to instantiate collection " + argumentClass);
		}
	}
	
	private Object[] lookupOrCreateArguments(Type[] argumentTypes) throws InstantiationException,
															IllegalAccessException,
															NoSuchMethodException,
															InvocationTargetException {
		Object[] arguments = new Object[argumentTypes.length];
		
		// loop over all arguments and see if we have a test value
		for (int index = 0; index < argumentTypes.length; index++) {
			Type type = argumentTypes[index];
			
			arguments[index] = lookupOrCreateArgument(type);
		}
		return arguments;
	}

	private Object lookupOrCreateArgument(Type type) throws InstantiationException,
													 IllegalAccessException,
													 NoSuchMethodException,
													 InvocationTargetException {
		Object argument;
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class<?> argumentClass = (Class<?>)parameterizedType.getRawType();
			
			if (Collection.class.isAssignableFrom(argumentClass)) {
				Collection<Object> collection = createCollectionFrom(argumentClass);

				Type[] rawTypes = ((ParameterizedType)type).getActualTypeArguments();
				
				collection.add( lookupOrCreateObject(rawTypes[0]) );
				collection.add( lookupOrCreateObject(rawTypes[0]) );
				
				argument = collection;
			} else if (Map.class.isAssignableFrom(argumentClass)) {
				Map<Object, Object> map = createMapFrom(argumentClass);
				Type[] rawTypes = ((ParameterizedType)type).getActualTypeArguments();
				Type rawKey = rawTypes[0];
				Type rawValue = rawTypes[1];
				
				Object key = lookupOrCreateObject(rawKey);
				Object value = lookupOrCreateObject(rawValue);
				
				map.put(key, value);
				argument = map;
			} else {
				argument = createObject(argumentClass);
			}
		
		} else {
			argument = lookupOrCreateObject(type);
		}
		return argument;
	}

	private Object lookupOrCreateObject(Type rawKey) throws NoSuchMethodException,
											   InstantiationException,
											   IllegalAccessException,
											   InvocationTargetException {
		Object key;
		if (testValues.containsKey(rawKey)) {
			key = testValues.get((Class<?>)rawKey).get();
		} else {
			key = createObject((Class<?>)rawKey);
		}
		return key;
	}
	
	private void testMethod(Object pojo, MethodDescriptor methodDescriptor) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		try {
			Object response = invokeMethod(pojo, methodDescriptor.getMethod());
			
			logger.info("Invoked custom method: " + pojo.getClass().getSimpleName() + "." + methodDescriptor.getName() + " = " + response);
		} catch (IllegalArgumentException ex) {
			logger.log(Level.WARNING, "Skipping method: " + methodDescriptor.getMethod() + " due to " + ex.getMessage(), ex);
		}
	}
	
	private void testProperty(Object pojo, PropertyDescriptor propertyDescriptor) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class<?> propertyType = propertyDescriptor.getPropertyType();

		Method writeMethod = propertyDescriptor.getWriteMethod();
		Method readMethod = propertyDescriptor.getReadMethod();
		
		if (readMethod == null) {
			try {
				readMethod = pojo.getClass().getMethod(
						"is" + Character.toUpperCase(propertyDescriptor.getName().charAt(0)) + propertyDescriptor.getName().substring(1),
						(Class<?> []) null);
			}
			catch (NoSuchMethodException x) {
				logger.info("wanted is" + Character.toUpperCase(propertyDescriptor.getName().charAt(0)) + propertyDescriptor.getName().substring(1));
			}
		}
		
		if (readMethod != null && writeMethod != null) {
			Object testValue = null;
			
			if (propertyType == List.class) {
				// http://www.coderanch.com/t/383648/java/java/java-reflection-element-type-List
				Field field = pojo.getClass().getDeclaredField(propertyDescriptor.getName());
				Type fieldType = field.getGenericType();
				if (fieldType instanceof ParameterizedType) {
					ParameterizedType pType = (ParameterizedType) fieldType;
					testValue = lookupOrCreateArgument(pType);
				}
			}
			else {
				if (testValues.containsKey(propertyType)) {
					testValue = testValues.get(propertyType).get();
				}
			}
			
			if (testValue == null) {
				try {
					testValue = createObject(propertyType);
				} catch (Exception ex) {
					logger.warning(ex.getMessage());
				}
			}
			
			if (testValue == null) {
				return;
			}
			
			writeMethod.invoke(pojo, testValue);
			Assert.assertEquals(propertyDescriptor.getPropertyType() + "." + propertyDescriptor.getName() + " property failed", readMethod.invoke(pojo), testValue);
		} else if (readMethod != null) {
			Object object = readMethod.invoke(pojo);
			logger.info("Getter " + pojo.getClass().getSimpleName() + "." + readMethod.getName() + " = " + object);
			
		}
	}
	

	private Object invokeMethod(Object pojo, Method method) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Type[] parameterTypes = method.getGenericParameterTypes();
		
		Object[] arguments = lookupOrCreateArguments(parameterTypes);
		
		// no exception was thrown, so let's try to invoke the method
		return method.invoke(pojo, arguments);
	}
	
}
