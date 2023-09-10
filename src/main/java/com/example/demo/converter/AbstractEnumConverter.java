
package com.example.demo.converter;

import com.example.demo.domain.PersistableEnum;
import jakarta.persistence.AttributeConverter;

/*
source:
https://stackoverflow.com/questions/23564506/is-it-possible-to-write-a-generic-enum-converter-for-jpa

NOTE TO SOURCE AS COMMENT SUGGESTS:

removed @Converter annotation from AbstractEnumConverter and
@Convert(converter = IndOrientation.Converter.class) from IndOrientation 
field and instead of that I added @Converter(autoApply = true) to concrete 
implementation Converter

Hibernate interprets the @Converter annotation as saying that it should 
instantiate your class and register it, but it's an abstract class with no 
default constructor (your missing <init>()). Perhaps Hibernate ought to 
realize and exclude annotated abstract classes, but it doesn't, so you should 
remove the annotation from the abstract class and add it to the base classes

*/
public abstract class AbstractEnumConverter
		<T extends Enum<T> & PersistableEnum<E>, E>
		implements AttributeConverter<T, E> {
	
    private final Class<T> clazz;

    public AbstractEnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }
	
	public Class<T> getClazz() {
		return clazz;
	}

    @Override
    public E convertToDatabaseColumn(T attribute) {
        return attribute != null
			? attribute.getValue()
			: null;
    }

    @Override
    public T convertToEntityAttribute(E dbData) {
        T[] enums = clazz.getEnumConstants();

        for (T e : enums) {
            if (e.getValue().equals(dbData)) {
                return e;
            }
        }

        throw new UnsupportedOperationException();
    }
}
