package tabby.dal.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tabby.common.bean.ref.ClassReference;

/**
 * @author wh1t3P1g
 * @since 2021/1/8
 */
@Repository
public interface ClassRepository extends CrudRepository<ClassReference, String> {

    @Query(value = "select * from CLASSES where NAME = :name limit 1", nativeQuery = true)
    ClassReference findClassReferenceByName(String name);

    @Query(value = "select count(*) from CLASSES", nativeQuery = true)
    int countAll();

    @Query(value = "CALL CSVWRITE(:path, 'SELECT * FROM CLASSES')", nativeQuery = true)
    void save2Csv(@Param("path") String path);

}
