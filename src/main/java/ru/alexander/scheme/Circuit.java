package ru.alexander.scheme;

import ru.alexander.scheme.components.*;

import java.util.*;

public class Circuit {
    private final Component[] components;

    private final Map<String, List<Component>> nodes;
    private final List<List<Component>> cycles;

    private final Queue<String[]> execution;

    public Circuit(Component... components) {
        this.components = components;

        nodes = new HashMap<>();
        nodeBuilder();

        cycles = new ArrayList<>();
        cycleBuilder();

        execution = new LinkedList<>();
        calculateQueue();
    }

    private void nodeBuilder() {
        for (int i = 0; i < components.length; i++)
            for (int j = 0; j < components[i].getPinLinks().length; j++) {
                String id = components[i].getPinLinks()[j];
                if (!nodes.containsKey(id)) nodes.put(id, new ArrayList<>());
                nodes.get(id).add(components[i]);
            }
    }
    private void cycleBuilder() {
        List<Component> traveled = new ArrayList<>();
        Stack<Path> paths = new Stack<>();
        paths.add(new Path(components[0], 0));
        while (!paths.isEmpty()) {
            Path path = paths.pop();
            if (!traveled.contains(path.component))
                traveled.add(path.component);

            String pinLink = path.component.getPinLinks()[1 - path.pin];
            List<Component> list = nodes.get(pinLink);

            Stack<Path> waiting = new Stack<>();
            boolean isCycle = false;
            for (Component component : list) {
                int idx = 0;
                for (int j = 1; j < component.getPinLinks().length; j++)
                    if (pinLink.equals(component.getPinLinks()[j])) {
                        idx = j;
                        break;
                    }

                if (!traveled.contains(component)) {
                    Path p = new Path(component, idx);
                    if (!paths.contains(p))
                        waiting.add(p);
                }
                if (idx == path.rootPin && component == path.root) {
                    if (path.parent != null) {
                        List<Component> cycle = new ArrayList<>();
                        Path p = path;
                        while (p != null) {
                            cycle.add(p.component);
                            p = p.parent;
                        }

                        boolean unique = true;
                        for (int i = 0; i < cycle.size(); i++) {
                            if (cycles.contains(cycle)) {
                                unique = false;
                                break;
                            }
                            for (List<Component> components : cycles) {
                                int index = cycle.indexOf(components.get(0));
                                if (cycle.size() >= index + components.size()) {
                                    boolean seq = true;
                                    for (int j = 1; j < components.size(); j++)
                                        if (cycle.get(index + j) != components.get(j)) {
                                            seq = false;
                                            break;
                                        }

                                    if (seq) unique = false;
                                }
                            }
                            Collections.reverse(cycle);

                            if (cycles.contains(cycle)) {
                                unique = false;
                                break;
                            }
                            for (List<Component> components : cycles) {
                                int index = cycle.indexOf(components.get(0));
                                if (cycle.size() >= index + components.size()) {
                                    boolean seq = true;
                                    for (int j = 1; j < components.size(); j++)
                                        if (cycle.get(index + j) != components.get(j)) {
                                            seq = false;
                                            break;
                                        }

                                    if (seq) unique = false;
                                }
                            }
                            Collections.reverse(cycle);

                            cycle.add(cycle.remove(0));
                        }
                        if (unique)
                            cycles.add(cycle);
                        isCycle = true;
                        break;
                    }
                } else if (!path.contains(component))
                    waiting.add(new Path(path, component, idx));

            }
            if (!isCycle) paths.addAll(waiting);
        }
    }

    private void calculateQueue() {
        for (Component component : components) component.resetUpdates();
        boolean[] executed = new boolean[components.length];
        boolean allUpdated;
        do {
            allUpdated = true;
            for (int i = 0; i < components.length; i++) {
                if (!components[i].calculate(0)) allUpdated = false;
                else {
                    if (!executed[i]) {
                        execution.add(new String[]{"calc", String.valueOf(i)});
                        executed[i] = true;
                    }
                    if (!components[i].isUpdated()) allUpdated = false;
                }
            }
            boolean updated;
            do {
                updated = false;
                for (Map.Entry<String, List<Component>> entry : nodes.entrySet()) {
                    List<Component> node = entry.getValue();
                    int count = 0;
                    Component uc = null;
                    for (Component component : node)
                        if (!component.updatedI) {
                            count++;
                            uc = component;
                        }
                    if (count == 1) {
                        uc.updatedI = true;
                        execution.add(new String[]{ "node", entry.getKey() });
                        updated = true;
                    }
                }
            } while(updated);
            do {
                updated = false;
                for (int i = 0; i < cycles.size(); i++) {
                    List<Component> cycle = cycles.get(i);
                    int count = 0;
                    Component uc = null;
                    for (Component component : cycle)
                        if (!component.updatedU) {
                            count++;
                            uc = component;
                        }
                    if (count == 1) {
                        uc.updatedU = true;
                        execution.add(new String[]{ "cycle", String.valueOf(i) });
                        updated = true;
                    }
                }
            } while(updated);
        } while (!allUpdated);

        for (int i = 0; i < components.length; i++)
            components[i].U = components[i].I = 0;
    }

    public void calculate(double dt) {
        for (Component component : components) component.resetUpdates();
        int size = execution.size();
        for (int i = 0; i < size; i++) {
            String[] command = execution.poll();
            switch (command[0]) {
                case "node" -> {
                    List<Component> list = nodes.get(command[1]);
                    int notUpdatedCount = 0;
                    int update = -1;
                    for (int j = 0; j < list.size(); j++)
                        if (!list.get(j).updatedI) {
                            notUpdatedCount++;
                            update = j;
                        }
//                    if (notUpdatedCount != 1) throw new IllegalStateException("Exists multiple unknown variables");
                    double sum = 0;
                    for (int j = 0; j < update; j++) sum += list.get(j).I;
                    for (int j = update + 1; j < list.size(); j++) sum += list.get(j).I;
                    list.get(update).I = -sum;
                    list.get(update).updatedI = true;
                }
                case "cycle" -> {
                    List<Component> list = cycles.get(Integer.parseInt(command[1]));
                    int notUpdatedCount = 0;
                    int update = -1;
                    for (int j = 0; j < list.size(); j++)
                        if (!list.get(j).updatedU) {
                            notUpdatedCount++;
                            update = j;
                        }
//                    if (notUpdatedCount != 1) throw new IllegalStateException("Exists multiple unknown variables");
                    double sum = 0;
                    for (int j = 0; j < update; j++) sum += list.get(j).U;
                    for (int j = update + 1; j < list.size(); j++) sum += list.get(j).U;
                    list.get(update).U = -sum;
                    list.get(update).updatedU = true;
                }
                case "calc" -> components[Integer.parseInt(command[1])].calculate(dt);
                default -> throw new IllegalStateException("Unexpected value: " + command[0]);
            }
            execution.add(command);
        }
    }

    public Map<String, List<Component>> getNodes() {
        return nodes;
    }

    public List<List<Component>> getCycles() {
        return cycles;
    }

    public Component[] getComponents() {
        return components;
    }

    private static class Path {
        public Component root;
        public int rootPin;
        public Path parent;
        public Component component;
        public int pin;

        public Path(Component component, int pin) {
            this.component = component;
            this.rootPin = pin;
            parent = null;
            root = component;
            this.pin = pin;
        }

        public Path(Path parent, Component component, int pin) {
            root = parent.root;
            rootPin = parent.rootPin;
            this.parent = parent;
            this.component = component;
            this.pin = pin;
        }
        public boolean contains(Component component) {
            Path p = this;
            while (p != null) {
                if (p.component == component)
                    return true;
                p = p.parent;
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Path path = (Path) o;
            return Objects.equals(root, path.root) && Objects.equals(parent, path.parent) && Objects.equals(component, path.component);
        }
    }
}
