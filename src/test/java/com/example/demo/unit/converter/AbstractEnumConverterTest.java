
package com.example.demo.unit.converter;

import com.example.demo.converter.AbstractEnumConverter;
import com.example.demo.domain.PersistableEnum;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/*
tests do not inherit @Test annotation from abstract classes on @Override methods
*/

@TestInstance(PER_CLASS)
public abstract class AbstractEnumConverterTest<T extends Enum<T> & PersistableEnum<E>, E> {
	
	protected AbstractEnumConverter<T, E> converter;
	
	public AbstractEnumConverterTest(AbstractEnumConverter<T, E> converter) {
		this.converter = converter;
	}
	
	@Test
	public final void convertingNullEnumToDatabaseColumnReturnsNullTest() {
		assertEquals(
			null, converter.convertToDatabaseColumn(null)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideEnumsAndEnumValues")
	public final void convertingNonNullEnumToDatabaseColumnReturnsEnumFieldValueTest(
			final T attribute, final E dbData) {
		
		assertEquals(
			dbData, converter.convertToDatabaseColumn(attribute)
		);
	}
	
	@Test
	public final void convertingNullToEntityAttributeThrowsTest() {
		assertThrows(
			UnsupportedOperationException.class,
			() -> converter.convertToEntityAttribute(null)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideEnumsAndEnumValues")
	public final void convertingValidEnumFieldValueToEntityAttributeReturnsTheEnumTest(
			final T attribute, final E dbData) {
		
		assertEquals(
			attribute, converter.convertToEntityAttribute(dbData)
		);
	}
	
	public abstract void convertingInValidEnumFieldValueToEntityAttributeThrowsTest();
	
	private Stream<Arguments> provideEnumsAndEnumValues() {
		final Class<T> clazz = converter.getClazz();
		return Stream.of(clazz.getEnumConstants())
			.map(attribute -> Arguments.of(attribute, attribute.getValue()));
	}
} 
