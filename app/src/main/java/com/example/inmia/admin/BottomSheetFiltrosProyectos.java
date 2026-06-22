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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.inmia.R;
import com.example.inmia.models.ProyectoFilter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BottomSheetFiltrosProyectos extends BottomSheetDialogFragment {

    public interface OnFiltrosAplicadosListener {
        void onFiltrosAplicados(ProyectoFilter filter);
    }

    private OnFiltrosAplicadosListener listener;
    private ProyectoFilter currentFilter;

    private ChipGroup chipGroupEstados;
    private Chip chipEnPlanos, chipEnPreventa, chipEnVenta, chipEntregado;
    private RangeSlider sliderPrecio, sliderArea, sliderDormitorios;
    private TextView tvPrecioRango, tvAreaRango, tvDormitoriosRango;
    private Spinner spinnerTipologias, spinnerOrdenar;
    private LinearLayout layoutOtrosFiltrosHeader, layoutOtrosFiltros;
    private ImageView ivArrowOtrosFiltros;
    private SwitchMaterial switchPetFriendly, switchAscensor, switchTerraza, switchBalcon;
    private SwitchMaterial switchAireAcondicionado, switchCocinaIntegrada, switchAmueblado, switchPersianas;
    private MaterialButton btnLimpiar, btnAplicar;
    private ImageView btnCerrar;

    public static BottomSheetFiltrosProyectos newInstance(ProyectoFilter filter) {
        BottomSheetFiltrosProyectos fragment = new BottomSheetFiltrosProyectos();
        fragment.currentFilter = filter;
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
        return inflater.inflate(R.layout.bottom_sheet_filtros_proyectos, container, false);
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
        chipGroupEstados = view.findViewById(R.id.chipGroupEstados);
        chipEnPlanos = view.findViewById(R.id.chipEnPlanos);
        chipEnPreventa = view.findViewById(R.id.chipEnPreventa);
        chipEnVenta = view.findViewById(R.id.chipEnVenta);
        chipEntregado = view.findViewById(R.id.chipEntregado);

        sliderPrecio = view.findViewById(R.id.sliderPrecio);
        sliderArea = view.findViewById(R.id.sliderArea);
        sliderDormitorios = view.findViewById(R.id.sliderDormitorios);
        tvPrecioRango = view.findViewById(R.id.tvPrecioRango);
        tvAreaRango = view.findViewById(R.id.tvAreaRango);
        tvDormitoriosRango = view.findViewById(R.id.tvDormitoriosRango);

        spinnerTipologias = view.findViewById(R.id.spinnerTipologias);
        spinnerOrdenar = view.findViewById(R.id.spinnerOrdenar);

        layoutOtrosFiltrosHeader = view.findViewById(R.id.layoutOtrosFiltrosHeader);
        layoutOtrosFiltros = view.findViewById(R.id.layoutOtrosFiltros);
        ivArrowOtrosFiltros = view.findViewById(R.id.ivArrowOtrosFiltros);

        switchPetFriendly = view.findViewById(R.id.switchPetFriendly);
        switchAscensor = view.findViewById(R.id.switchAscensor);
        switchTerraza = view.findViewById(R.id.switchTerraza);
        switchBalcon = view.findViewById(R.id.switchBalcon);
        switchAireAcondicionado = view.findViewById(R.id.switchAireAcondicionado);
        switchCocinaIntegrada = view.findViewById(R.id.switchCocinaIntegrada);
        switchAmueblado = view.findViewById(R.id.switchAmueblado);
        switchPersianas = view.findViewById(R.id.switchPersianas);

        btnLimpiar = view.findViewById(R.id.btnLimpiarFiltros);
        btnAplicar = view.findViewById(R.id.btnAplicarFiltros);
        btnCerrar = view.findViewById(R.id.btnCerrarFiltros);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> tipologiasAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.tipologias_min_options, android.R.layout.simple_spinner_item);
        tipologiasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipologias.setAdapter(tipologiasAdapter);

        ArrayAdapter<CharSequence> ordenarAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.ordenar_por_options, android.R.layout.simple_spinner_item);
        ordenarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrdenar.setAdapter(ordenarAdapter);
    }

    private void loadCurrentFilter() {
        if (currentFilter == null) {
            currentFilter = new ProyectoFilter();
            currentFilter.getEstados().add("En planos");
            currentFilter.getEstados().add("En preventa");
            currentFilter.getEstados().add("En venta");
        }

        chipEnPlanos.setChecked(currentFilter.getEstados().contains("En planos"));
        chipEnPreventa.setChecked(currentFilter.getEstados().contains("En preventa"));
        chipEnVenta.setChecked(currentFilter.getEstados().contains("En venta"));
        chipEntregado.setChecked(currentFilter.getEstados().contains("Entregado"));

        sliderPrecio.setValues(
                (float) Math.max(0, Math.min(2000000, currentFilter.getPrecioMin())),
                (float) Math.max(0, Math.min(2000000, currentFilter.getPrecioMax()))
        );
        updatePrecioLabel();

        sliderArea.setValues(
                (float) Math.max(0, Math.min(500, currentFilter.getAreaMin())),
                (float) Math.max(0, Math.min(500, currentFilter.getAreaMax()))
        );
        updateAreaLabel();

        sliderDormitorios.setValues(
                (float) Math.max(1, Math.min(5, currentFilter.getDormitoriosMin())),
                (float) Math.max(1, Math.min(5, currentFilter.getDormitoriosMax()))
        );
        updateDormitoriosLabel();

        spinnerTipologias.setSelection(getTipologiasIndex(currentFilter.getTipologiasMin()));
        spinnerOrdenar.setSelection(getOrdenarIndex(currentFilter.getOrdenarPor()));

        switchPetFriendly.setChecked(currentFilter.isPetFriendly());
        switchAscensor.setChecked(currentFilter.isConAscensor());
        switchTerraza.setChecked(currentFilter.isConTerraza());
        switchBalcon.setChecked(currentFilter.isConBalcon());
        switchAireAcondicionado.setChecked(currentFilter.isConAireAcondicionado());
        switchCocinaIntegrada.setChecked(currentFilter.isConCocinaIntegrada());
        switchAmueblado.setChecked(currentFilter.isAmueblado());
        switchPersianas.setChecked(currentFilter.isConPersianas());
    }

    private void setupListeners() {
        sliderPrecio.addOnChangeListener((slider, valueFrom, valueTo) -> updatePrecioLabel());
        sliderArea.addOnChangeListener((slider, valueFrom, valueTo) -> updateAreaLabel());
        sliderDormitorios.addOnChangeListener((slider, valueFrom, valueTo) -> updateDormitoriosLabel());

        layoutOtrosFiltrosHeader.setOnClickListener(v -> toggleOtrosFiltros());

        btnCerrar.setOnClickListener(v -> dismiss());

        btnLimpiar.setOnClickListener(v -> {
            currentFilter = new ProyectoFilter();
            currentFilter.getEstados().add("En planos");
            currentFilter.getEstados().add("En preventa");
            currentFilter.getEstados().add("En venta");
            loadCurrentFilter();
        });

        btnAplicar.setOnClickListener(v -> {
            applyFilters();
            dismiss();
        });
    }

    private void updatePrecioLabel() {
        List<Float> values = sliderPrecio.getValues();
        if (values.size() >= 2) {
            double min = values.get(0);
            double max = values.get(1);
            tvPrecioRango.setText(String.format(Locale.getDefault(), "S/ %,.0f - S/ %,.0f", min, max));
        }
    }

    private void updateAreaLabel() {
        List<Float> values = sliderArea.getValues();
        if (values.size() >= 2) {
            int min = values.get(0).intValue();
            int max = values.get(1).intValue();
            tvAreaRango.setText(String.format(Locale.getDefault(), "%d - %d m²", min, max));
        }
    }

    private void updateDormitoriosLabel() {
        List<Float> values = sliderDormitorios.getValues();
        if (values.size() >= 2) {
            int min = values.get(0).intValue();
            int max = values.get(1).intValue();
            if (min == max) {
                tvDormitoriosRango.setText(String.format(Locale.getDefault(), "%d dormitorios", min));
            } else {
                tvDormitoriosRango.setText(String.format(Locale.getDefault(), "%d - %d dormitorios", min, max));
            }
        }
    }

    private void toggleOtrosFiltros() {
        if (layoutOtrosFiltros.getVisibility() == View.GONE) {
            layoutOtrosFiltros.setVisibility(View.VISIBLE);
            ivArrowOtrosFiltros.setRotation(180);
        } else {
            layoutOtrosFiltros.setVisibility(View.GONE);
            ivArrowOtrosFiltros.setRotation(0);
        }
    }

    private int getTipologiasIndex(int min) {
        if (min <= 0) return 0;
        if (min == 1) return 1;
        if (min == 2) return 2;
        if (min == 3) return 3;
        return 4;
    }

    private int getOrdenarIndex(String orden) {
        if (orden == null) return 0;
        switch (orden) {
            case ProyectoFilter.ORDEN_PRECIO_MAYOR_MENOR: return 1;
            case ProyectoFilter.ORDEN_PRECIO_MENOR_MAYOR: return 2;
            case ProyectoFilter.ORDEN_NOMBRE_A_Z: return 3;
            case ProyectoFilter.ORDEN_NOMBRE_Z_A: return 4;
            default: return 0;
        }
    }

    private void applyFilters() {
        currentFilter = new ProyectoFilter();

        List<String> estados = new ArrayList<>();
        if (chipEnPlanos.isChecked()) estados.add("En planos");
        if (chipEnPreventa.isChecked()) estados.add("En preventa");
        if (chipEnVenta.isChecked()) estados.add("En venta");
        if (chipEntregado.isChecked()) estados.add("Entregado");
        currentFilter.setEstados(estados);

        List<Float> precioValues = sliderPrecio.getValues();
        if (precioValues.size() >= 2) {
            currentFilter.setPrecioMin(precioValues.get(0));
            currentFilter.setPrecioMax(precioValues.get(1));
        }

        List<Float> areaValues = sliderArea.getValues();
        if (areaValues.size() >= 2) {
            currentFilter.setAreaMin(areaValues.get(0).intValue());
            currentFilter.setAreaMax(areaValues.get(1).intValue());
        }

        List<Float> dormitoriosValues = sliderDormitorios.getValues();
        if (dormitoriosValues.size() >= 2) {
            currentFilter.setDormitoriosMin(dormitoriosValues.get(0).intValue());
            currentFilter.setDormitoriosMax(dormitoriosValues.get(1).intValue());
        }

        currentFilter.setTipologiasMin(getTipologiasMinFromIndex(spinnerTipologias.getSelectedItemPosition()));
        currentFilter.setOrdenarPor(getOrdenarFromIndex(spinnerOrdenar.getSelectedItemPosition()));

        currentFilter.setPetFriendly(switchPetFriendly.isChecked());
        currentFilter.setConAscensor(switchAscensor.isChecked());
        currentFilter.setConTerraza(switchTerraza.isChecked());
        currentFilter.setConBalcon(switchBalcon.isChecked());
        currentFilter.setConAireAcondicionado(switchAireAcondicionado.isChecked());
        currentFilter.setConCocinaIntegrada(switchCocinaIntegrada.isChecked());
        currentFilter.setAmueblado(switchAmueblado.isChecked());
        currentFilter.setConPersianas(switchPersianas.isChecked());

        if (listener != null) {
            listener.onFiltrosAplicados(currentFilter);
        }
    }

    private int getTipologiasMinFromIndex(int index) {
        switch (index) {
            case 1: return 1;
            case 2: return 2;
            case 3: return 3;
            case 4: return 5;
            default: return 0;
        }
    }

    private String getOrdenarFromIndex(int index) {
        switch (index) {
            case 1: return ProyectoFilter.ORDEN_PRECIO_MAYOR_MENOR;
            case 2: return ProyectoFilter.ORDEN_PRECIO_MENOR_MAYOR;
            case 3: return ProyectoFilter.ORDEN_NOMBRE_A_Z;
            case 4: return ProyectoFilter.ORDEN_NOMBRE_Z_A;
            default: return null;
        }
    }
}
