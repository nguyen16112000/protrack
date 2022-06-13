package com.ban.protrack.component;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@Component
@AllArgsConstructor
public class Graph<T>{
    Map<T, Vector<T>> adj;

    public Graph() {
        this.adj = new HashMap<T, Vector<T>>();
    }

    public Vector<T> getVertex(T u){
        if (adj.containsKey(u))
            return adj.get(u);
        return new Vector<T>();
    }

    public void addVertex(T u, T v) {
        if (adj.containsKey(u)){
            Vector<T> vector = adj.get(u);
            vector.add(v);
            adj.replace(u, vector);
        }
        else {
            Vector<T> vector = new Vector<>();
            vector.add(v);
            adj.put(u, vector);
        }
    }

    public void printGraph(){
        for (T u : adj.keySet()) {
            System.out.println(u + "->" + adj.get(u));
        }
    }
}
