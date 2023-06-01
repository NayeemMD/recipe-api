package com.food.integrationtests.util

class TestUtil {

   static String findFieldMessage(messages, filterMessages) {
        return   messages.stream().filter {it.contains(filterMessages)}.findFirst().get()
    }
}
