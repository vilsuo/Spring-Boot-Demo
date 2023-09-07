
package com.example.demo.unit.converter;

import com.example.demo.converter.AbstractEnumConverter;
import com.example.demo.domain.PersistableEnum;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public abstract class ConverterTest<T extends Enum<T> & PersistableEnum<E>, E> {
	
	protected AbstractEnumConverter<T, E> converter;
	
	@Test
	public final void convertingNullEnumToDatabaseColumnReturnsNullTest() {
		assertEquals(
			null, converter.convertToDatabaseColumn(null)
		);
	}
	
	@Test
	public abstract void convertingNonNullEnumToDatabaseColumnReturnsEnumFieldValueTest();
	
	@Test
	public final void convertingNullToEntityAttributeThrowsTest() {
		assertThrows(
			UnsupportedOperationException.class,
			() -> converter.convertToEntityAttribute(null)
		);
	}
	
	@Test
	public abstract void convertingValidEnumFieldValueToEntityAttributeReturnsTheEnumTest();
	
	@Test
	public abstract void convertingInValidEnumFieldValueToEntityAttributeThrowsTest();
}
