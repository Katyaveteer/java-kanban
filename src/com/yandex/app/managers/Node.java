package com.yandex.app.managers;

import com.yandex.app.model.Task;

class Node {
    Node next;
    Node prev;
    Task task;

    public Node(Task task, Node prev, Node next) {
        this.next = next;
        this.prev = prev;
        this.task = task;
    }
}
