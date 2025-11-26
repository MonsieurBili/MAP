package Service;

import Domain.Ducks.Duck;
import Domain.Friendship;
import Domain.Person.Persoana;
import Domain.User;
import Repository.Database.RepositoryDuckDB;
import Repository.Database.RepositoryPersonDB;
import Repository.FriendshipRepository;
import Repository.IdGenerator;
import Repository.RepositoryDuck;
import Repository.RepositoryPerson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class ServiceStatistics {
    RepositoryDuckDB repositoryDuck;
    RepositoryPersonDB repositoryPerson;
    FriendshipRepository friendshipRepository;
    public ServiceStatistics(RepositoryDuckDB repositoryDuck, RepositoryPersonDB repositoryPerson, FriendshipRepository friendshipRepository, String filename) {
        this.repositoryDuck = repositoryDuck;
        this.repositoryPerson = repositoryPerson;
        this.friendshipRepository = friendshipRepository;
        loaddata(filename);
    }
    public void loaddata(String filename)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String line;
            IdGenerator idGenerator= IdGenerator.getInstance();
            while ((line = br.readLine()) != null) {
                String parts[] =line.split(",");
                Long id1 = Long.parseLong(parts[0]);
                Long id2 = Long.parseLong(parts[1]);
                User user1 = repositoryDuck.findOne(id1);
                User user2 = repositoryDuck.findOne(id2);
                if (user1 == null)
                    user1 = repositoryPerson.findOne(id1);
                if (user2 == null)
                    user2 = repositoryPerson.findOne(id2);
                Friendship friendship = new Friendship(user1,user2);
                friendship.setId(idGenerator.nextId());
                friendshipRepository.save(friendship);

            }

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public int CommunityNumber()
    {
        Set<User>allUsers = getAllUsers();
        if (allUsers.size()==0)
            return 0;
        Map<User,List<User>>graph = createGraph();

        Set<User> visited = new HashSet<>();
        int components = 0;
        for (User user : allUsers) {
            if (!visited.contains(user)) {
                dfs(user, graph, visited);
                components++;
            }
        }
        return components;
    }

    private void dfs(User node, Map<User, List<User>> graph, Set<User> visited) {
        visited.add(node);
        for (User neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, graph, visited);
            }
        }
    }

    public Set<User> getAllUsers()
    {
        Set<User> allUsers = new HashSet<>();
        for (Persoana p : repositoryPerson.findAll())
            allUsers.add(p);
        for (Duck d: repositoryDuck.findAll())
            allUsers.add(d);
        return allUsers;
    }

    private Map<User, List<User>> createGraph()
    {
        Set<User>allUsers = getAllUsers();
        Map<User, List<User>> graph = new HashMap<>();
        for (User u : allUsers)
            graph.put(u, new ArrayList<>());
        for (Friendship f : friendshipRepository.findAll()) {
            User user1 = f.getUser1();
            User user2 = f.getUser2();
            if (allUsers.contains(user1) && allUsers.contains(user2)) {
                graph.get(user1).add(user2);
                graph.get(user2).add(user1);
            }
        }
        return graph;
    }
    public void showComponentWithMaxDiameter() {
        Map<User, List<User>> graph = createGraph();
        List<Set<User>> components = getAllComponents(graph);
        Set<User> bestComp = null;
        int maxD = -1;
        for (Set<User> comp : components) {
            int d = diameter(comp, graph);
            if (d > maxD)
            {
                maxD = d;
                bestComp = comp;
            }
        }
        System.out.println("Max diamter:" + maxD);
        List<User> list = new ArrayList<>(bestComp);
        list.sort(Comparator.comparingLong(User::getId));
        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            System.out.print(u.getUsername() + "(id=" + u.getId() + ")");
            if (i < list.size()-1) System.out.print(", ");
        }
        System.out.println();
    }
    private int diameter(Set<User> comp, Map<User, List<User>> adj) {
        if (comp.size() <= 1) return 0;
        List<User> list = new ArrayList<>(comp);
        int res = 0;
        for (int i = 0; i < list.size(); i++) {
            boolean[] visited = new boolean[list.size()];
            res = Math.max(res, farthestNode(i, visited, list, adj, 0));
        }
        return res;
    }
    private int farthestNode(int idx, boolean[] visited, List<User> list,
                             Map<User, List<User>> adj, int dist) {
        if (visited[idx]) return 0;
        visited[idx] = true;
        User curr = list.get(idx);
        int maxD = dist;
        for (User nei : adj.get(curr)) {
            if (list.contains(nei)) {
                int neiIdx = list.indexOf(nei);
                if (!visited[neiIdx]) {
                    maxD = Math.max(maxD, farthestNode(neiIdx, visited, list, adj, dist + 1));
                }
            }
        }
        return maxD;
    }
    private List<Set<User>> getAllComponents(Map<User, List<User>> graph) {
        Set<User> visited = new HashSet<>();
        List<Set<User>> components = new ArrayList<>();

        for (User u : graph.keySet()) {
            if (!visited.contains(u)) {
                Set<User> comp = new HashSet<>();
                dfsCollect(u, graph, visited, comp);
                components.add(comp);
            }
        }
        return components;
    }

    private void dfsCollect(User node, Map<User, List<User>> graph,
                            Set<User> visited, Set<User> component) {
        visited.add(node);
        component.add(node);
        for (User nn : graph.get(node)) {
            if (!visited.contains(nn)) {
                dfsCollect(nn, graph, visited, component);
            }
        }
    }
}
