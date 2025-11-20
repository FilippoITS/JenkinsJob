import static org.junit.jupiter.api.Assertions.*;

import com.job.utilities.Conversion;
import com.job.dto.JobDTO;
import com.job.entity.Job;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.time.LocalDateTime;

public class ConversionTest {

    LocalDateTime startDate = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
    LocalDateTime endDate = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());

    @Test
    public void testConvertToDTO() {
        // Crea un oggetto Job con dati di esempio
        Job job = new Job(1, "Project A", startDate, endDate, "In Progress", "Some job data");


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
