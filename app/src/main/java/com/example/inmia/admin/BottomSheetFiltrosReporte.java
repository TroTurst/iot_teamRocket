package com.example.inmia.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.inmia.R;
import com.example.inmia.models.ReporteFilter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFiltrosReporte extends BottomSheetDialogFragment {

    public interface OnFiltrosAplicadosListener {
        void onFiltrosAplicados(ReporteFilter filter);
    }

    private OnFiltrosAplicadosListener listener;
    private ReporteFilter currentFilter;

    private ChipGroup chipGroupPeriodo;
    private Chip chipPeriodoUltimoMes, chipPeriodoUltimos3, chipPeriodoEsteAnio, chipPeriodoTodos;
    private ChipGroup chipGroupVistaFiltro;
    private Chip chipVistaPorProyecto, chipVistaPorAsesor;
    private Spinner spinnerDistrito;
    private Spinner spinnerAsesor;
    private LinearLayout contenedorAsesor;
    private ChipGroup chipGroupEstado;
    private Chip chipEstadoEnProceso, chipEstadoAprobada, chipEstadoPagada, chipEstadoRechazada;
    private MaterialButton btnLimpiar, btnAplicar;
    private ImageView btnCerrar;

    private final List<String> distritosDisponibles = new ArrayList<>();
    private final List<String> asesoresDisponibles   = new ArrayList<>();
    private final List<String> asesoresNombres      = new ArrayList<>();

    public static BottomSheetFiltrosReporte newInstance(
            ReporteFilter filter,
            List<String> distritos,
            List<String> asesoresIds,
            List<String> asesoresNombres) {
        BottomSheetFiltrosReporte fragment = new BottomSheetFiltrosReporte();
        fragment.currentFilter = filter;
        if (distritos != null) fragment.distritosDisponibles.addAll(distritos);
        if (asesoresIds != null) fragment.asesoresDisponibles.addAll(asesoresIds);
        if (asesoresNombres != null) fragment.asesoresNombres.addAll(asesoresNombres);
        return fragment;
    }

    public void setListener(OnFiltrosAplicadosListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filtros_reporte, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.Theme_Inmia_BottomSheet);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupSpinners();
        loadCurrentFilter();
        setupListeners();
    }

    private void initViews(View view) {
        chipGroupPeriodo      = view.findViewById(R.id.chipGroupPeriodoReporte);
        chipPeriodoUltimoMes  = view.findViewById(R.id.chipPeriodoUltimoMes);
        chipPeriodoUltimos3   = view.findViewById(R.id.chipPeriodoUltimos3);
        chipPeriodoEsteAnio   = view.findViewById(R.id.chipPeriodoEsteAnio);
        chipPeriodoTodos      = view.findViewById(R.id.chipPeriodoTodos);

        chipGroupVistaFiltro  = view.findViewById(R.id.chipGroupVistaFiltroReporte);
        chipVistaPorProyecto  = view.findViewById(R.id.chipVistaPorProyecto);
        chipVistaPorAsesor    = view.findViewById(R.id.chipVistaPorAsesor);

        spinnerDistrito   = view.findViewById(R.id.spinnerDistritoFiltroReporte);
        spinnerAsesor     = view.findViewById(R.id.spinnerAsesorFiltroReporte);
        contenedorAsesor  = view.findViewById(R.id.contenedorAsesorFiltroReporte);

        chipGroupEstado       = view.findViewById(R.id.chipGroupEstadoFiltroReporte);
        chipEstadoEnProceso   = view.findViewById(R.id.chipEstadoEnProceso);
        chipEstadoAprobada    = view.findViewById(R.id.chipEstadoAprobada);
        chipEstadoPagada      = view.findViewById(R.id.chipEstadoPagada);
        chipEstadoRechazada   = view.findViewById(R.id.chipEstadoRechazada);

        btnLimpiar  = view.findViewById(R.id.btnLimpiarFiltrosReporte);
        btnAplicar  = view.findViewById(R.id.btnAplicarFiltrosReporte);
        btnCerrar   = view.findViewById(R.id.btnCerrarFiltrosReporte);
    }

    private void setupSpinners() {
        ArrayAdapter<String> distritosAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, distritosDisponibles);
        distritosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrito.setAdapter(distritosAdapter);

        ArrayAdapter<String> asesoresAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, asesoresNombres);
        asesoresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAsesor.setAdapter(asesoresAdapter);
    }

    private void loadCurrentFilter() {
        if (currentFilter == null) {
            currentFilter = new ReporteFilter();
        }

        switch (currentFilter.getPeriodo()) {
            case ReporteFilter.PERIODO_ULTIMO_MES: chipPeriodoUltimoMes.setChecked(true); break;
            case ReporteFilter.PERIODO_ULTIMOS_3:  chipPeriodoUltimos3.setChecked(true);  break;
            case ReporteFilter.PERIODO_ESTE_ANIO:  chipPeriodoEsteAnio.setChecked(true);  break;
            case ReporteFilter.PERIODO_TODOS:      chipPeriodoTodos.setChecked(true);     break;
        }

        if (currentFilter.getVista() == ReporteFilter.VISTA_POR_ASESOR) {
            chipVistaPorAsesor.setChecked(true);
            contenedorAsesor.setVisibility(View.VISIBLE);
        } else {
            chipVistaPorProyecto.setChecked(true);
            contenedorAsesor.setVisibility(View.GONE);
        }

        if (currentFilter.getDistrito() != null && !currentFilter.getDistrito().isEmpty()) {
            int idx = distritosDisponibles.indexOf(currentFilter.getDistrito());
            spinnerDistrito.setSelection(idx >= 0 ? idx : 0);
        }

        if (currentFilter.getAsesorId() != null && !currentFilter.getAsesorId().isEmpty()) {
            int idx = asesoresDisponibles.indexOf(currentFilter.getAsesorId());
            spinnerAsesor.setSelection(idx >= 0 ? idx : 0);
        }

        List<String> estados = currentFilter.getEstados();
        if (estados != null) {
            chipEstadoEnProceso.setChecked(estados.contains("En proceso"));
            chipEstadoAprobada.setChecked(estados.contains("Aprobada"));
            chipEstadoPagada.setChecked(estados.contains("Pagada"));
            chipEstadoRechazada.setChecked(estados.contains("Rechazada"));
        }
    }

    private void setupListeners() {
        chipGroupVistaFiltro.setOnCheckedStateChangeListener((group, checkedIds) -> {
            boolean esAsesor = checkedIds.contains(R.id.chipVistaPorAsesor);
            contenedorAsesor.setVisibility(esAsesor ? View.VISIBLE : View.GONE);
        });

        btnCerrar.setOnClickListener(v -> dismiss());

        btnLimpiar.setOnClickListener(v -> {
            currentFilter.reset();
            loadCurrentFilter();
        });

        btnAplicar.setOnClickListener(v -> {
            applyFilters();
            dismiss();
        });
    }

    private void applyFilters() {
        currentFilter = new ReporteFilter();

        if (chipPeriodoUltimoMes.isChecked())      currentFilter.setPeriodo(ReporteFilter.PERIODO_ULTIMO_MES);
        else if (chipPeriodoUltimos3.isChecked())  currentFilter.setPeriodo(ReporteFilter.PERIODO_ULTIMOS_3);
        else if (chipPeriodoEsteAnio.isChecked())  currentFilter.setPeriodo(ReporteFilter.PERIODO_ESTE_ANIO);
        else if (chipPeriodoTodos.isChecked())     currentFilter.setPeriodo(ReporteFilter.PERIODO_TODOS);

        if (chipVistaPorAsesor.isChecked()) currentFilter.setVista(ReporteFilter.VISTA_POR_ASESOR);
        else                                currentFilter.setVista(ReporteFilter.VISTA_POR_PROYECTO);

        if (spinnerDistrito.getSelectedItemPosition() > 0) {
            currentFilter.setDistrito(distritosDisponibles.get(spinnerDistrito.getSelectedItemPosition()));
        }

        if (spinnerAsesor.getSelectedItemPosition() > 0) {
            currentFilter.setAsesorId(asesoresDisponibles.get(spinnerAsesor.getSelectedItemPosition()));
        }

        List<String> estados = new ArrayList<>();
        if (chipEstadoEnProceso.isChecked())  estados.add("En proceso");
        if (chipEstadoAprobada.isChecked())   estados.add("Aprobada");
        if (chipEstadoPagada.isChecked())     estados.add("Pagada");
        if (chipEstadoRechazada.isChecked())  estados.add("Rechazada");
        currentFilter.setEstados(estados);

        if (listener != null) {
            listener.onFiltrosAplicados(currentFilter);
        }
    }
}
