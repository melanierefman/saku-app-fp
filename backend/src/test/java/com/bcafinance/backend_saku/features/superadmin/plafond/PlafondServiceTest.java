package com.bcafinance.backend_saku.features.superadmin.plafond;

import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlafondServiceTest {

    @Mock
    private PlafondRepository plafondRepository;

    @InjectMocks
    private PlafondService plafondService;

    private Plafond testPlafond;
    private PlafondRequest testRequest;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        testPlafond = new Plafond();
        testPlafond.setId(testId);
        testPlafond.setNama("Plafond Gold");
        testPlafond.setMinPendapatan(new BigDecimal("5000000.00"));
        testPlafond.setPlafondMaksimal(new BigDecimal("20000000.00"));
        testPlafond.setMinPlafond(new BigDecimal("2000000.00"));
        testPlafond.setMaxPlafond(new BigDecimal("20000000.00"));
        testPlafond.setMinSkor(60);
        testPlafond.setMaxSkor(100);
        testPlafond.setBunga(new BigDecimal("0.05"));
        testPlafond.setBiayaAdmin(new BigDecimal("50000.00"));
        testPlafond.setStatus(true);
        testPlafond.setCreatedDate(LocalDateTime.now());

        testRequest = new PlafondRequest();
        testRequest.setNama("Plafond Gold Updated");
        testRequest.setMinPendapatan(new BigDecimal("5000000.00"));
        testRequest.setPlafondMaksimal(new BigDecimal("25000000.00"));
        testRequest.setMinPlafond(new BigDecimal("2000000.00"));
        testRequest.setMaxPlafond(new BigDecimal("25000000.00"));
        testRequest.setMinSkor(65);
        testRequest.setMaxSkor(100);
        testRequest.setBunga(new BigDecimal("0.04"));
        testRequest.setBiayaAdmin(new BigDecimal("50000.00"));
        testRequest.setStatus(true);
    }

    @Test
    @DisplayName("create - should save and return created plafond response")
    void create_ShouldSaveAndReturnResponse() {
        when(plafondRepository.save(any(Plafond.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlafondResponse response = plafondService.create(testRequest);

        assertThat(response).isNotNull();
        assertThat(response.getNama()).isEqualTo("Plafond Gold Updated");
        assertThat(response.getPlafondMaksimal()).isEqualByComparingTo(new BigDecimal("25000000.00"));
        verify(plafondRepository, times(1)).save(any(Plafond.class));
    }

    @Test
    @DisplayName("create - should throw when minPlafond > maxPlafond")
    void create_ShouldThrowWhenMinPlafondGreaterThanMaxPlafond() {
        testRequest.setMinPlafond(new BigDecimal("30000000.00"));
        testRequest.setMaxPlafond(new BigDecimal("10000000.00"));

        assertThatThrownBy(() -> plafondService.create(testRequest))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("Minimum plafond tidak boleh lebih besar");

        verify(plafondRepository, never()).save(any());
    }

    @Test
    @DisplayName("create - should throw when minSkor > maxSkor")
    void create_ShouldThrowWhenMinSkorGreaterThanMaxSkor() {
        testRequest.setMinSkor(90);
        testRequest.setMaxSkor(50);

        assertThatThrownBy(() -> plafondService.create(testRequest))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("Minimum skor tidak boleh lebih besar");

        verify(plafondRepository, never()).save(any());
    }

    @Test
    @DisplayName("findAll - should return list of plafond responses")
    void findAll_ShouldReturnList() {
        when(plafondRepository.findAll()).thenReturn(List.of(testPlafond));

        List<PlafondResponse> result = plafondService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNama()).isEqualTo("Plafond Gold");
    }

    @Test
    @DisplayName("findById - should return plafond response when found")
    void findById_ShouldReturnPlafondWhenFound() {
        when(plafondRepository.findById(testId)).thenReturn(Optional.of(testPlafond));

        PlafondResponse result = plafondService.findById(testId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        assertThat(result.getNama()).isEqualTo("Plafond Gold");
    }

    @Test
    @DisplayName("findById - should throw when not found")
    void findById_ShouldThrowWhenNotFound() {
        when(plafondRepository.findById(testId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plafondService.findById(testId))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("Plafond tidak ditemukan");
    }

    @Test
    @DisplayName("update - should update and return response")
    void update_ShouldUpdateAndReturnResponse() {
        when(plafondRepository.findById(testId)).thenReturn(Optional.of(testPlafond));
        when(plafondRepository.save(any(Plafond.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlafondResponse result = plafondService.update(testId, testRequest);

        assertThat(result).isNotNull();
        assertThat(result.getNama()).isEqualTo("Plafond Gold Updated");
        verify(plafondRepository, times(1)).save(testPlafond);
    }

    @Test
    @DisplayName("delete - should delete existing plafond")
    void delete_ShouldDeleteExisting() {
        when(plafondRepository.findById(testId)).thenReturn(Optional.of(testPlafond));
        doNothing().when(plafondRepository).delete(testPlafond);

        plafondService.delete(testId);

        verify(plafondRepository, times(1)).delete(testPlafond);
    }

    @Test
    @DisplayName("calculateApprovedAmount - should return APPROVED and calculated WSM percentage for score >= 75")
    void calculateApprovedAmount_Score75_ShouldReturnApproved100Percent() {
        when(plafondRepository.findAllByStatusTrue()).thenReturn(List.of(testPlafond));

        PlafondCalculationResponse result = plafondService.calculateApprovedAmount(new BigDecimal("7000000.00"), 85.0);

        assertThat(result).isNotNull();
        assertThat(result.getKeputusan()).isEqualTo("APPROVED");
        assertThat(result.getPersentaseApproval()).isEqualTo(79);
        assertThat(result.getApprovedAmount()).isEqualByComparingTo(new BigDecimal("16000000.00"));
    }

    @Test
    @DisplayName("calculateApprovedAmount - should return REVIEW and calculated WSM percentage for score >= 60 and < 75")
    void calculateApprovedAmount_Score65_ShouldReturnReview70Percent() {
        when(plafondRepository.findAllByStatusTrue()).thenReturn(List.of(testPlafond));

        PlafondCalculationResponse result = plafondService.calculateApprovedAmount(new BigDecimal("7000000.00"), 65.0);

        assertThat(result).isNotNull();
        assertThat(result.getKeputusan()).isEqualTo("REVIEW");
        assertThat(result.getPersentaseApproval()).isEqualTo(54);
        assertThat(result.getApprovedAmount()).isEqualByComparingTo(new BigDecimal("11500000.00"));
    }

    @Test
    @DisplayName("calculateApprovedAmount - should return REJECTED and calculated WSM percentage for score < 60")
    void calculateApprovedAmount_Score50_ShouldReturnRejected50Percent() {
        when(plafondRepository.findAllByStatusTrue()).thenReturn(List.of());
        when(plafondRepository.findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(new BigDecimal("7000000.00")))
                .thenReturn(Optional.of(testPlafond));

        PlafondCalculationResponse result = plafondService.calculateApprovedAmount(new BigDecimal("7000000.00"), 50.0);

        assertThat(result).isNotNull();
        assertThat(result.getKeputusan()).isEqualTo("REJECTED");
        assertThat(result.getPersentaseApproval()).isEqualTo(48);
        assertThat(result.getApprovedAmount()).isEqualByComparingTo(new BigDecimal("10500000.00"));
    }

    @Test
    @DisplayName("calculateApprovedAmount - should throw when no active plafond found")
    void calculateApprovedAmount_ShouldThrowWhenNoPlafondAvailable() {
        when(plafondRepository.findAllByStatusTrue()).thenReturn(List.of());
        when(plafondRepository.findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(any()))
                .thenReturn(Optional.empty());
        when(plafondRepository.findFirstByStatusTrueOrderByMinSkorAsc())
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> plafondService.calculateApprovedAmount(new BigDecimal("3000000.00"), 80.0))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("Data master plafond aktif belum tersedia");
    }
}
