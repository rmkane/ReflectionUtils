package com.rmkane.reflection;
import java.util.Calendar;
import java.util.Set;

import org.junit.Test;

import com.google.gson.GsonBuilder;

public class FieldGrabberTest {
	private static interface Foo {
		String getUniqueId();
		Calendar getTimestamp();
	}
	
	private static abstract class Bar implements Foo {
		private String uniqueId;
		private Calendar timestamp;
		public String getUniqueId() { return uniqueId; }
		public Calendar getTimestamp() { return timestamp; }
	}
	
	private static class Baz extends Bar {
		private int count;
		@SuppressWarnings("unused") public int getCount() { return count; }
		@Override public Calendar getTimestamp() { return super.getTimestamp(); }
	}

	@Test
	public void testFields() {
		Set<FieldGrabber.Field> fields = FieldGrabber.getterFields(Baz.class);

		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(fields));
	}
}
