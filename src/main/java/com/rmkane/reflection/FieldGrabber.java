package com.rmkane.reflection;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.Set;

import org.reflections.ReflectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

public class FieldGrabber {
	private static final String GET_PREFIX = "get";
	private static final Predicate<? super Method> IS_PUBLIC = ReflectionUtils.withModifier(Modifier.PUBLIC);
	private static final Predicate<? super Method> IS_GETTER = ReflectionUtils.withPrefix(GET_PREFIX);
	private static final Predicate<? super Method> NOT_IGNORE = Predicates.not(ReflectionUtils.withAnnotation(JsonIgnore.class));

	private static final FieldComparator FIELD_COMPARE = new FieldComparator();
	private static final Function<Method, Field> TO_FIELD = new Function<Method, Field>() {
		@Override
		public Field apply(Method method) {
			return new Field(formatFieldName(method), formatFieldType(method));
		}
	};

	@SuppressWarnings("unchecked")
	public static Set<Field> getterFields(Class<?> clazz) {
		return fieldInfo(ReflectionUtils.getAllMethods(clazz, IS_PUBLIC, IS_GETTER));
	}

	private static Set<Field> fieldInfo(Set<Method> methods) {
		return FluentIterable.from(methods).filter(NOT_IGNORE).transform(TO_FIELD).toSortedSet(FIELD_COMPARE);
	}

	private static String formatFieldName(Method method) {
		return Introspector.decapitalize(method.getName().replaceFirst(GET_PREFIX, ""));
	}

	private static String formatFieldType(Method method) {
		String type = method.getReturnType().getSimpleName().toLowerCase();

		switch (type) {
			case "int":
			case "integer":
			case "long":
				return "int";
			case "double":
			case "float":
				return "number";
			case "date":
			case "calendar":
				return "date";
			case "char":
			case "character":
			case "string":
				return "string";
			case "boolean":
				return "boolean";
			default:
				return "object";
		}
	}

	public static class Field {
		private String name;
		private String type;

		public Field(String name, String type) {
			super();
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			Field other = (Field) obj;

			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			if (type == null) {
				if (other.type != null) {
					return false;
				}
			} else if (!type.equals(other.type)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return String.format("Field [name=%s, type=%s]", name, type);
		}
	}

	private static class FieldComparator implements Comparator<Field> {
		@Override
		public int compare(Field fieldA, Field fieldB) {
			if (fieldB == null) {
				return -1;
			}
			if (fieldA == null) {
				return 1;
			}
			if (fieldA.equals(fieldB)) {
				return 0;
			}

			int result = fieldA.getName().compareToIgnoreCase(fieldB.getName());

			if (result != 0) {
				return result;
			}

			return fieldA.getType().compareTo(fieldB.getType());
		}
	}

}
