import static org.junit.jupiter.api.Assertions.*;

import com.job.dto.JobDTO;
import com.job.entity.Job;
import org.junit.jupiter.api.Test;
import java.util.Date;

public class ConversionTest {

    @Test
    public void testConvertToDTO() {
        // Crea un oggetto Job con dati di esempio
        Job job = new Job();
        job.setId(1L);
        job.setProject("Project A");
        job.setStartdate(new Date());
        job.setEnddate(new Date());
        job.setStatus("In Progress");
        job.setData("Some job data");

        // Esegui la conversione
        JobDTO jobDTO = Conversion.convertToDTO(job);

        // Verifica che i valori nel DTO siano corretti
        assertEquals(job.getId(), jobDTO.getId());
        assertEquals(job.getProject(), jobDTO.getProject());
        assertEquals(job.getStartdate(), jobDTO.getStartdate());
        assertEquals(job.getEnddate(), jobDTO.getEnddate());
        assertEquals(job.getStatus(), jobDTO.getStatus());
        assertEquals(job.getData(), jobDTO.getData());
    }

}
