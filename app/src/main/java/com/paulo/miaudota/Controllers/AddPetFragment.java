package com.paulo.miaudota.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paulo.miaudota.InputFilterMinMax;
import com.paulo.miaudota.Models.Cidade;
import com.paulo.miaudota.Models.Estado;
import com.paulo.miaudota.Models.Pet;
import com.paulo.miaudota.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AddPetFragment extends Fragment implements View.OnClickListener {

    private ArrayList<String> estadosSpinner = new ArrayList<>();
    private SearchableSpinner spinnerCidades = null;
    private Spinner spinnerEstados, spinnerTipo ,spinnerGenero, spinnerTamanho;
    private String ibgeEstados, ufPet, cidadePet, tipoPet ,generoPet, tamanhoPet, userId, petId, petImageStr, isAdotado;
    private Uri petImageUri;
    private Bitmap bitmap = null;
    private EditText descricaoEditText, nomePetEditText, idadeAnosEditText, idadeMesesEditText, dddEditText, celularEditText;
    private Button btnAddpet;
    private ProgressBar progressBar;
    private ImageView petImage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String nomePet, idadeAnos, idadeMeses, descricao, dataCadastro, ddd, celular;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    public void onStart() {
        Log.i("Warning_Activity","onStart addPet -> ");
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Log.i("Warning_Activity","onStart EditProfile userNull-> ");
            startActivity(new Intent(getActivity(), WelcomeScreen.class));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("Warning_Activity","onCreateView -> MessageFragment");

        View view = inflater.inflate(R.layout.fragment_add_pet, container, false);
        petImage = view.findViewById(R.id.petImageAdd);
        petImage.setOnClickListener(this);
        nomePetEditText = view.findViewById(R.id.nomePetAdd);
        spinnerTipo = view.findViewById(R.id.selectTipoPet);
        idadeAnosEditText = view.findViewById(R.id.idadeAnosAdd);
        idadeAnosEditText.setFilters(new InputFilter[]{new InputFilterMinMax("0", "50")});
        idadeMesesEditText = view.findViewById(R.id.idadeMesesAdd);
        idadeMesesEditText.setFilters(new InputFilter[]{new InputFilterMinMax("0", "12")});
        spinnerGenero = view.findViewById(R.id.selectGeneroAdd);
        spinnerTamanho = view.findViewById(R.id.selectTamanhoAdd);
        spinnerEstados = view.findViewById(R.id.selectEstadoAdd);
        spinnerCidades = view.findViewById(R.id.selectCidadeAdd);
        spinnerCidades.setTitle("Selecione a cidade");
        spinnerCidades.setPositiveButton("OK");
        dddEditText = view.findViewById(R.id.dddAdd);
        dddEditText.setFilters(new InputFilter[]{new InputFilterMinMax("0", "99")});
        celularEditText = view.findViewById(R.id.celularAdd);
        descricaoEditText = view.findViewById(R.id.descricaoAdd);
        btnAddpet = view.findViewById(R.id.btnAddPet);
        btnAddpet.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progressBarAdd);
        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        isAdotado = "false";

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Pets");

        ibgeEstados = buscasSegundoPlano("estado");
        loadSpinners();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddPet:
                PegarInfos();
                break;
            case R.id.petImageAdd:
                addPetImage();
                break;
        }
    }

    ActivityResultLauncher<Intent> petImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        petImageUri = data.getData();

                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(petImageUri);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                        }
                        catch (Exception ex){
                            Log.e("TAG","falhou bitmap: ", ex);
                        }

                        Glide.with(getActivity())
                                .load(petImageUri.toString()) // image url
                                .placeholder(R.drawable.pet_add_placeholder) // any placeholder to load at start
                                .error(R.drawable.pet_add_placeholder)  // any image in case of error
                                .override(200, 200) // resizing
                                .centerCrop()
                                .into(petImage);  // imageview object
                    }
                }
            });

    private void addPetImage() {
        Intent openGalleryIntent = new Intent();
        openGalleryIntent.setType("image/*");
        openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        petImageActivityResultLauncher.launch(openGalleryIntent);
    }


    private void solicitaCidades(String siglaEstado) {

        String ibgeCidades = buscasSegundoPlano("cidade", siglaEstado);

        Gson jsonCidades = new GsonBuilder().setPrettyPrinting().create();
        Cidade[] cidades = jsonCidades.fromJson(String.valueOf(ibgeCidades), Cidade[].class);

        ArrayList<String> cidadesSpinner = new ArrayList<>();

        for(Cidade cidade: cidades){
            cidadesSpinner.add(cidade.getNome());
        }

        cidadesSpinner.add(0,"Cidade");

        ArrayAdapter<String> adapterCidades = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_style,
                cidadesSpinner){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerCidades.setAdapter(adapterCidades);

        spinnerCidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cidadePet = spinnerCidades.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressBar.setVisibility(View.INVISIBLE);

    }

    private String buscasSegundoPlano(String ... params) {

        String respostaIbge = null;
        SegundoPlano segundoPlano = new SegundoPlano();
        try {
            respostaIbge = segundoPlano.execute(params).get();
        }catch (ExecutionException | InterruptedException e){
            Log.e("onPost", "Erro resposta IBGE: " + e);
        }

        return respostaIbge;

    }

    private void loadSpinners(){

        //region Estados
            Gson jsonEstados = new GsonBuilder().setPrettyPrinting().create();
            Estado[] estados = jsonEstados.fromJson(String.valueOf(ibgeEstados), Estado[].class);

            for(Estado estado: estados){
                estadosSpinner.add(estado.getSigla());
            }
            estadosSpinner.add(0,"UF");

            ArrayAdapter<String> adapterEstados = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_style,
                    estadosSpinner){
                @Override
                public boolean isEnabled(int position){
                    if(position == 0)
                    {
                        // Disable the second item from Spinner
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position==0) {
                        // Set the first item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            spinnerEstados.setAdapter(adapterEstados);

            spinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    solicitaCidades(estadosSpinner.get(i));
                    ufPet = spinnerEstados.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        //endregion

        //region TipoPet
            ArrayAdapter<String> adapterTiposPets = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_style,
                    getResources().getStringArray(R.array.pets_types)){
                @Override
                public boolean isEnabled(int position){
                    if(position == 0)
                    {
                        // Disable the first item from Spinner
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

            spinnerTipo.setAdapter(adapterTiposPets);
            spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    tipoPet = spinnerTipo.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //por favor selecione
                }
            });

        //endregion

        //region Genero

            ArrayAdapter<String> adapterGeneroPet = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_style,
                    getResources().getStringArray(R.array.pets_gender)){
                @Override
                public boolean isEnabled(int position){
                    if(position == 0)
                    {
                        // Disable the first item from Spinner
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

            spinnerGenero.setAdapter(adapterGeneroPet);
            spinnerGenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    generoPet = spinnerGenero.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //por favor selecione
                }
            });

        //endregion

        //region Tamanho

            ArrayAdapter<String> adapterTamanhoPet = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_style,
                    getResources().getStringArray(R.array.pets_sizes)){
                @Override
                public boolean isEnabled(int position){
                    if(position == 0)
                    {
                        // Disable the first item from Spinner
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

            spinnerTamanho.setAdapter(adapterTamanhoPet);
            spinnerTamanho.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    tamanhoPet = spinnerTamanho.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //por favor selecione
                }
            });

        //endregion

    }

    private void PegarInfos(){

        progressBar.setVisibility(View.VISIBLE);
        nomePet = nomePetEditText.getText().toString().trim();
        idadeAnos = idadeAnosEditText.getText().toString().trim();
        idadeMeses = idadeMesesEditText.getText().toString().trim();
        descricao = descricaoEditText.getText().toString().trim();
        petId = databaseReference.push().getKey();
        dataCadastro = sdf.format(Calendar.getInstance().getTime());
        ddd = dddEditText.getText().toString().trim();
        celular = celularEditText.getText().toString().trim();

        if(!validarPreenchimento(nomePet, idadeAnos, idadeMeses, descricao, ddd, celular)){
            Toast.makeText(getContext(), "Por favor preencha todos os campos !", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        //faz upload da imagem e depois envia os dados
        if(bitmap != null){
            handleUpload(bitmap);
        }

    }

    private void AddPetDb(){
        Pet petModel = new Pet(petImageStr ,nomePet, tipoPet, idadeAnos, idadeMeses ,generoPet, tamanhoPet, ufPet, cidadePet, descricao, petId, dataCadastro, userId, ddd, celular, isAdotado);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(petId).setValue(petModel);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Pet adicionado com sucesso !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), Home.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Erro ao enviar dados do pet !", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("petsImages")
                .child(petId + ".jpeg");

        ref.putBytes(baos.toByteArray())
                .addOnSuccessListener(taskSnapshot -> getDownloadUrl(ref))
                .addOnFailureListener(e -> Log.e("TAG","onFailure: ", e.getCause() ));
    }

    private void getDownloadUrl(StorageReference sReference){
        sReference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Log.i("TAG","onSuccess: " + uri );
                    petImageStr = uri.toString();
                    AddPetDb();
                });
    }


    private boolean validarPreenchimento(String nomePet, String idadeAnos, String idadeMeses, String descricao, String ddd, String celular) {

        if(nomePet.isEmpty()){
            nomePetEditText.setError("Campo obrigatório !!");
            nomePetEditText.requestFocus();
            return false;
        }

        if(tipoPet.equals("Tipo") || tipoPet == null){
            ((TextView)spinnerTipo.getSelectedView()).setError("Error message");
            return false;
        }

        if(idadeAnos.isEmpty()){
            idadeAnosEditText.setError("Campo obrigatório !!");
            idadeAnosEditText.requestFocus();
            return false;
        }

        if(idadeMeses.isEmpty()){
            idadeMesesEditText.setError("Campo obrigatório !!");
            idadeMesesEditText.requestFocus();
            return false;
        }

        if(generoPet.equals("Gênero") || generoPet == null){
            ((TextView)spinnerGenero.getSelectedView()).setError("Error message");
            return false;
        }

        if(tamanhoPet.equals("Tamanho") || tamanhoPet == null){
            ((TextView)spinnerTamanho.getSelectedView()).setError("Error message");
            return false;
        }

        if(ufPet.equals("UF") || ufPet == null){
            ((TextView)spinnerEstados.getSelectedView()).setError("Error message");
            return false;
        }

        if(cidadePet.equals("Cidade") || cidadePet == null){
            ((TextView)spinnerCidades.getSelectedView()).setError("Error message");
            return false;
        }

        if(ddd.isEmpty()){
            dddEditText.setError("Campo obrigatório !!");
            dddEditText.requestFocus();
            return false;
        }

        if(celular.isEmpty()){
            celularEditText.setError("Campo obrigatório !!");
            celularEditText.requestFocus();
            return false;
        }

        if(descricao.isEmpty()){
            descricaoEditText.setError("Campo obrigatório !!");
            descricaoEditText.requestFocus();
            return false;
        }

        if(descricao.length() > 240){
            Toast.makeText(getContext(), "A descrição deve conter no máximo 240 caracteres!", Toast.LENGTH_SHORT).show();
            descricaoEditText.requestFocus();
            return false;
        }

        if(petImageUri == null){
            Toast.makeText(getContext(), "Por favor adicione uma imagem do pet !", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

}
