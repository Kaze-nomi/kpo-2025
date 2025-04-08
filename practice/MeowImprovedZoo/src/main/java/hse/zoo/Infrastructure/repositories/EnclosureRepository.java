package hse.zoo.Infrastructure.repositories;

import hse.zoo.Domain.entities.Enclosure;
import hse.zoo.Domain.interfaces.repositoryInterfaces.IEnclosureRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class EnclosureRepository implements IEnclosureRepository {

    private Integer id_counter = 0;

    private final Map<Integer, Enclosure> enclosures = new ConcurrentHashMap<>();

    @Override
    public Enclosure getEnclosure(Integer id) {
        if (!enclosures.containsKey(id)) {
            throw new IllegalArgumentException("Enclosure with id " + id + " not found");
        }
        return enclosures.get(id);
    }

    @Override
    public Integer addEnclosure(Enclosure enclosure) {
        enclosures.put(id_counter++, enclosure);
        return id_counter - 1;
    }

    @Override
    public Boolean deleteEnclosure(Integer id) {
        if (!enclosures.containsKey(id)) {
            throw new IllegalArgumentException("Enclosure with id " + id + " not found");
        }
        return enclosures.remove(id, enclosures.get(id));
    }

    @Override
    public List<Enclosure> getAllEnclosures() {
        return new ArrayList<>(enclosures.values());
    }

    @Override
    public Integer findEnclosure(Enclosure enclosure) {
        for (Map.Entry<Integer, Enclosure> entry : enclosures.entrySet()) {
            if (entry.getValue().equals(enclosure)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
}