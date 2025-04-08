package hse.zoo.Domain.interfaces.repositoryInterfaces;

import hse.zoo.Domain.entities.Enclosure;
import java.util.List;

public interface IEnclosureRepository {
    Integer addEnclosure(Enclosure enclosure);
    Enclosure getEnclosure(Integer id);
    Boolean deleteEnclosure(Integer id);
    List<Enclosure> getAllEnclosures();
    Integer findEnclosure(Enclosure enclosure);
}
