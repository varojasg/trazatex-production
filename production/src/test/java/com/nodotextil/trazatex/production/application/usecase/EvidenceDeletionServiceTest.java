package com.nodotextil.trazatex.production.application.usecase;
import com.nodotextil.trazatex.production.application.dto.Actor;
import com.nodotextil.trazatex.production.application.dto.Role;
import com.nodotextil.trazatex.production.application.port.out.*;
import com.nodotextil.trazatex.production.domain.model.Batch;
import com.nodotextil.trazatex.production.domain.model.EvidenceOwnerType;
import com.nodotextil.trazatex.sharedkernel.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class EvidenceDeletionServiceTest {
    final BatchRepositoryPort batches=mock(BatchRepositoryPort.class);
    final TransformationRepositoryPort transformations=mock(TransformationRepositoryPort.class);
    final EvidenceDeletionPort deletions=mock(EvidenceDeletionPort.class);
    final EvidenceStoragePort storage=mock(EvidenceStoragePort.class);
    final AuditLogPort audit=mock(AuditLogPort.class);
    final PlatformTransactionManager manager=mock(PlatformTransactionManager.class);
    final UUID owner=UUID.randomUUID(), id=UUID.randomUUID();
    final Actor actor=new Actor(UUID.randomUUID(),UUID.randomUUID(),Role.OPERATOR);
    EvidenceDeletionService service;
    @BeforeEach void prepare(){when(manager.getTransaction(any())).thenReturn(new SimpleTransactionStatus());service=new EvidenceDeletionService(batches,transformations,deletions,storage,audit,Clock.systemUTC(),manager);}
    @Test void foreignCompanyCannotDelete(){when(batches.findById(owner)).thenReturn(Optional.empty());assertThrows(NotFoundException.class,()->service.delete(EvidenceOwnerType.BATCH,owner,id,actor));verifyNoInteractions(deletions,storage);}
    @Test void recordsDurableDeletionBeforeContactingStorage(){Batch batch=mock(Batch.class);when(batch.companyId()).thenReturn(actor.companyId());when(batches.findById(owner)).thenReturn(Optional.of(batch));when(deletions.markDeleted(eq(EvidenceOwnerType.BATCH),eq(owner),eq(id),eq(actor.companyId()),any())).thenReturn(true);service.delete(EvidenceOwnerType.BATCH,owner,id,actor);verify(manager).commit(any());verifyNoInteractions(storage);}
    @Test void storageFailureKeepsDeletionForRetry(){when(deletions.nextDeleted(any())).thenReturn(Optional.of(id));doThrow(new IllegalStateException("sin red")).when(storage).delete(id.toString());assertThrows(IllegalStateException.class,()->service.purgeDeleted());verify(deletions,never()).purge(any());verify(manager).rollback(any());}
    @Test void deletesStorageBeforePurgingReferences(){when(deletions.nextDeleted(any())).thenReturn(Optional.of(id),Optional.empty());assertEquals(1,service.purgeDeleted());var order=inOrder(storage,deletions);order.verify(storage).delete(id.toString());order.verify(deletions).purge(id);}
    @Test void noDeletionDoesNotContactStorage(){when(deletions.nextDeleted(any())).thenReturn(Optional.empty());assertEquals(0,service.purgeDeleted());verifyNoInteractions(storage);}
}
