package com.example.inmia.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.inmia.R;
import com.example.inmia.models.AsesorFilter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFiltrosAsesores extends BottomSheetDialogFragment {

    public interface OnFiltrosAsesoresAplicadosListener {
        void onFiltrosAplicados(AsesorFilter filter);
    }

    private OnFiltrosAsesoresAplicadosListener listener;
    private AsesorFilter currentFilter;

    private Spinner spinnerOrdenar, spinnerDistrito;
    private TextInputEditText etMinCitas;
    private MaterialButton btnLimpiar, btnAplicar;

    private static final String[] ORDENAR_OPTIONS = {
            "Sin orden",
            "Nombre: A-Z",
            "Nombre: Z-A",
            "Más proyectos",
            "Menos proyectos",
            "Más citas",
            "Menos citas"
    };

    private static final String[] DISTRITOS = {
            "Todos",
            "Ancón", "Ate", "Barranco", "Breña",
            "Carabayllo", "Cercado de Lima", "Chorrillos", "Comas",
            "El Agustino", "Independencia", "Jesús María", "La Molina",
            "La Victoria", "Lince", "Los Olivos", "Magdalena del Mar",
            "Miraflores", "Pueblo Libre", "Puente Piedra", "Rímac",
            "San Borja", "San Isidro", "San Juan de Lurigancho", "San Juan de Miraflores",
            "San Luis", "San Martín de Porres", "San Miguel", "Santa Anita",
            "Santiago de Surco", "Surquillo", "Villa El Salvador", "Villa María del Triunfo"
    };

    public static BottomSheetFiltrosAsesores newInstance(AsesorFilter filter) {
        BottomSheetFiltrosAsesores fragment = new BottomSheetFiltrosAsesores();
        fragment.currentFilter = filter;
        return fragment;
    }

    public void setListener(OnFiltrosAsesoresAplicadosListener listener) {
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
        return inflater.inflate(R.layout.bottom_sheet_filtros_asesores, container, false);
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
        spinnerOrdenar = view.findViewById(R.id.spinnerOrdenarAsesores);
        spinnerDistrito = view.findViewById(R.id.spinnerDistritoAsesores);
        etMinCitas = view.findViewById(R.id.etMinCitasAsesores);
        btnLimpiar = view.findViewById(R.id.btnLimpiarFiltrosAsesores);
        btnAplicar = view.findViewById(R.id.btnAplicarFiltrosAsesores);

        View btnCerrar = view.findViewById(R.id.btnCerrarFiltrosAsesores);
        btnCerrar.setOnClickListener(v -> dismiss());
    }

    private void setupSpinners() {
        ArrayAdapter<String> ordenarAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, ORDENAR_OPTIONS);
        ordenarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrdenar.setAdapter(ordenarAdapter);

        ArrayAdapter<String> distritoAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, DISTRITOS);
        distritoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrito.setAdapter(distritoAdapter);
    }

    private void loadCurrentFilter() {
        if (currentFilter == null) {
            currentFilter = new AsesorFilter();
        }

        spinnerOrdenar.setSelection(getOrdenarIndex(currentFilter.getOrdenarPor()));
        spinnerDistrito.setSelection(getDistritoIndex(currentFilter.getDistrito()));
        etMinCitas.setText(String.valueOf(currentFilter.getMinCitas() > 0 ? currentFilter.getMinCitas() : ""));
    }

    private void setupListeners() {
        btnLimpiar.setOnClickListener(v -> {
            currentFilter = new AsesorFilter();
            loadCurrentFilter();
        });

        btnAplicar.setOnClickListener(v -> {
            applyFilters();
            dismiss();
        });
    }

    private int getOrdenarIndex(String orden) {
        if (orden == null) return 0;
        switch (orden) {
            case AsesorFilter.ORDEN_NOMBRE_A_Z: return 1;
            case AsesorFilter.ORDEN_NOMBRE_Z_A: return 2;
            case AsesorFilter.ORDEN_PROYECTOS_MAS_MENOS: return 3;
            case AsesorFilter.ORDEN_PROYECTOS_MENOS_MAS: return 4;
            case AsesorFilter.ORDEN_CITAS_MAS_MENOS: return 5;
            case AsesorFilter.ORDEN_CITAS_MENOS_MAS: return 6;
            default: return 0;
        }
    }

    private int getDistritoIndex(String distrito) {
        if (distrito == null || distrito.isEmpty()) return 0;
        for (int i = 0; i < DISTRITOS.length; i++) {
            if (DISTRITOS[i].equals(distrito)) return i;
        }
        return 0;
    }

    private void applyFilters() {
        currentFilter = new AsesorFilter();

        currentFilter.setOrdenarPor(getOrdenarFromIndex(spinnerOrdenar.getSelectedItemPosition()));

        int distritoIndex = spinnerDistrito.getSelectedItemPosition();
        if (distritoIndex > 0) {
            currentFilter.setDistrito(DISTRITOS[distritoIndex]);
        } else {
            currentFilter.setDistrito("");
        }

        String citasText = etMinCitas.getText() != null ? etMinCitas.getText().toString().trim() : "";
        int minCitas = 0;
        if (!citasText.isEmpty()) {
            try {
                minCitas = Integer.parseInt(citasText);
            } catch (NumberFormatException ignored) {}
        }
        currentFilter.setMinCitas(minCitas);

        if (listener != null) {
            listener.onFiltrosAplicados(currentFilter);
        }
    }

    private String getOrdenarFromIndex(int index) {
        switch (index) {
            case 1: return AsesorFilter.ORDEN_NOMBRE_A_Z;
            case 2: return AsesorFilter.ORDEN_NOMBRE_Z_A;
            case 3: return AsesorFilter.ORDEN_PROYECTOS_MAS_MENOS;
            case 4: return AsesorFilter.ORDEN_PROYECTOS_MENOS_MAS;
            case 5: return AsesorFilter.ORDEN_CITAS_MAS_MENOS;
            case 6: return AsesorFilter.ORDEN_CITAS_MENOS_MAS;
            default: return null;
        }
    }
}
