# Pasek narzędziowy, menu i nawigacja dolna

**Technik programista | klasa 5 | aplikacje mobilne**

`Toolbar`, menu opcji w pliku XML, strzałka powrotu, `BottomNavigationView`
i ekran ustawień.

---

## 1. Punkt wyjścia

Na poprzednich zajęciach przechodziłeś między ekranami przyciskiem umieszczonym
pośrodku układu. Działa, ale żadna prawdziwa aplikacja tak nie wygląda.

Standard jest inny i znasz go z każdej aplikacji w telefonie: **pasek u góry**
z tytułem bieżącego ekranu, strzałką powrotu po lewej i menu po prawej.
Czynności rzadsze chowają się pod trzema kropkami, częstsze dostają ikonę.
Na dole, gdy aplikacja ma kilka głównych sekcji, bywa **pasek nawigacji**.

Ta lekcja jest o budowaniu takiego interfejsu – i to ostatni element układu,
którego jeszcze nie umiesz zrobić.

---

## 2. Co masz w swoim projekcie

Zanim cokolwiek dopiszesz, musisz wiedzieć, czy Twoja aplikacja **już ma pasek**.
Od tego zależy cała reszta, a odpowiedź zależy od wersji szablonu, z którego
powstał projekt.

Istnieją dwie rzeczy o podobnej roli i różnym pochodzeniu:

| | `ActionBar` | `Toolbar` |
|---|---|---|
| Skąd się bierze | **z motywu**, automatycznie | **z Twojego układu**, jako zwykły widok |
| Gdzie jest w XML | nigdzie, nie widać go w layoucie | wpisujesz go do `activity_main.xml` |
| Położenie | zawsze na górze okna | gdziekolwiek – nawet na dole |
| Sterowanie | `getSupportActionBar()` | `findViewById` jak każdy widok |

**`Toolbar` jest nowszy i to jego używamy.** `ActionBar` nie jest błędem,
ale nie da się go przesunąć, ostylować ani umieścić w środku układu.

### Jak sprawdzić, co masz

Otwórz `res/values/themes.xml` i popatrz na atrybut `parent` pierwszego stylu:

```xml
<style name="Base.Theme.MojaAplikacja" parent="Theme.Material3.DayNight.NoActionBar">
```

| Nazwa motywu kończy się na | Znaczy to, że |
|---|---|
| **`.NoActionBar`** | paska **nie ma** – dodajesz własny `Toolbar` i nic nie psujesz |
| `.DarkActionBar` albo samo `.DayNight` | pasek **jest**, dostajesz go z motywu |

Jeśli nie masz `NoActionBar`, a mimo to dodasz własny `Toolbar` i wywołasz
`setSupportActionBar`, aplikacja zgaśnie z komunikatem:

```
java.lang.IllegalStateException: This Activity already has an action bar
supplied by the window decor. Do not request Window.FEATURE_SUPPORT_ACTION_BAR
```

Czyli po polsku: „ten ekran już ma pasek, nie proś o drugi".

**Rozwiązanie:** dopisz `.NoActionBar` do nazwy motywu nadrzędnego,
w `themes.xml` **i w wersji nocnej**, jeśli projekt ją ma:

```xml
<!-- było -->
parent="Theme.Material3.DayNight"
<!-- ma być -->
parent="Theme.Material3.DayNight.NoActionBar"
```

> Zwróć uwagę, że motywy tworzą rodzinę: `NoActionBar` to wariant tego samego
> motywu, a nie inny motyw. Kolory i czcionki zostają, znika tylko pasek.

---

## 3. Własny pasek w układzie

`Toolbar` jest zwykłym widokiem, więc wkładasz go do układu jak każdy inny –
na samej górze, zwykle z wysokością `?attr/actionBarSize`:

```xml
<androidx.appcompat.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    android:background="?attr/colorPrimary"
    app:titleTextColor="#FFFFFF" />
```

| Zapis | Znaczenie |
|---|---|
| `?attr/actionBarSize` | standardowa wysokość paska **z motywu**, a nie zgadnięte `56dp` |
| `?attr/colorPrimary` | główny kolor motywu – pasek zmieni się razem z resztą aplikacji |
| `app:titleTextColor` | kolor tytułu; przedrostek `app:`, bo atrybut pochodzi z biblioteki |

Znak zapytania przed `attr` znaczy „weź to z aktualnego motywu".
To nie jest odwołanie do konkretnej wartości jak `@color/`, tylko do **ustawienia
motywu** – dzięki temu ten sam układ wygląda poprawnie w trybie jasnym i ciemnym.

### Podpięcie w Javie

Sam `Toolbar` w układzie to jeszcze tylko pasek z tytułem. Żeby zachowywał się
jak pasek aplikacji – obsługiwał menu i strzałkę powrotu – trzeba go **zgłosić
systemowi**:

```java
Toolbar toolbar = findViewById(R.id.toolbar);
setSupportActionBar(toolbar);
```

Od tej chwili `getSupportActionBar()` zwraca Twój pasek.

> **Importuj `androidx.appcompat.widget.Toolbar`**, nie `android.widget.Toolbar`.
> Obie klasy istnieją, obie się kompilują, ale `setSupportActionBar` przyjmuje
> wyłącznie tę pierwszą. Ten sam rodzaj pomyłki co przy `AlertDialog`
> na poprzednich zajęciach.

---

## 4. Menu w pliku XML

Menu nie buduje się w kodzie, tylko opisuje w osobnym pliku zasobów –
tak samo jak układ ekranu.

**Prawy przycisk na `res` → New → Android Resource File**, typ zasobu: **Menu**,
nazwa `menu_main`. Powstanie katalog `res/menu/` i plik w środku.

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <item
        android:id="@+id/action_share"
        android:icon="@drawable/ic_share"
        android:title="@string/action_share"
        app:showAsAction="ifRoom" />

    <item
        android:id="@+id/action_clear"
        android:title="@string/action_clear"
        app:showAsAction="never" />

    <item
        android:id="@+id/action_about"
        android:title="@string/action_about"
        app:showAsAction="never" />

</menu>
```

| Atrybut | Znaczenie |
|---|---|
| `android:id` | identyfikator – po nim rozpoznasz kliknięcie |
| `android:title` | napis; **zawsze z `@string/`** |
| `android:icon` | ikona, widoczna tylko gdy pozycja jest na pasku |
| `app:showAsAction` | czy pozycja ma być na pasku, czy w menu rozwijanym |

Wartości `showAsAction`:

| Wartość | Efekt |
|---|---|
| `never` | zawsze pod trzema kropkami – **wartość domyślna i najczęstsza** |
| `ifRoom` | na pasku, jeśli jest miejsce; w przeciwnym razie pod kropkami |
| `always` | zawsze na pasku, nawet kosztem tytułu – używaj oszczędnie |
| `withText` | dodaje napis obok ikony; łączy się znakiem `\|`, np. `ifRoom\|withText` |

> **`app:showAsAction`, nie `android:showAsAction`.** To jest najczęstszy błąd
> w tym pliku. Wersja z przedrostkiem `android:` kompiluje się bez słowa skargi
> i **nie działa** – wszystkie pozycje lądują pod trzema kropkami, choćbyś
> wpisał `always`. Biblioteka zgodności obsługuje wyłącznie wariant `app:`,
> dlatego w nagłówku pliku musi być deklaracja `xmlns:app`.

Na pasku powinny lądować **najwyżej dwie, trzy pozycje**. Reszta pod kropkami.
Pasek ma mieścić jeszcze tytuł ekranu.

---

## 5. Podpięcie menu w aktywności

Dwie metody, obie nadpisywane.

### Pokazanie menu

```java
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
}
```

**Rozwijacz** (`MenuInflater`) robi z pliku XML gotowe obiekty – dokładnie to samo,
co `setContentView` robi z układem ekranu. Zwrócenie `true` znaczy „pokaż menu";
`false` ukrywa je w całości.

### Obsługa kliknięcia

```java
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_share) {
        share();
        return true;
    } else if (id == R.id.action_clear) {
        clearForm();
        return true;
    } else if (id == R.id.action_about) {
        openAbout();
        return true;
    }

    return super.onOptionsItemSelected(item);
}
```

Dwie rzeczy są tu istotne i obie wracają z wcześniejszych lekcji.

> **Znowu `if / else if`, a nie `switch`.** Identyfikatory w klasie `R` nie są
> stałymi, więc `switch (item.getItemId())` nie kompiluje się – ten sam błąd
> *constant expression required* co przy kalkulatorze. Każdy starszy tutorial
> i każdy agent podsunie Ci tu `switch`.

**Zwracana wartość ma znaczenie.** `return true` mówi „obsłużyłem, nie szukaj
dalej". Ostatnia linia – `return super.onOptionsItemSelected(item)` – przekazuje
nieobsłużone pozycje do klasy nadrzędnej. **Nie zastępuj jej przez `return false`**:
to właśnie klasa nadrzędna obsługuje strzałkę powrotu, o której za chwilę.

---

## 6. Ikony

Ikon nie rysujesz i nie pobierasz z internetu – Android Studio ma wbudowany zestaw.

**Prawy przycisk na `res` → New → Vector Asset → Clip Art**, wybierasz ikonę
z katalogu, nadajesz nazwę w konwencji `ic_nazwa` i ustawiasz kolor.

Powstaje plik `res/drawable/ic_share.xml` – **grafika wektorowa**, czyli opis
kształtu, a nie siatka pikseli. Skaluje się bez utraty jakości na każdym ekranie
i waży kilkaset bajtów zamiast kilku kilobajtów.

Żeby ikona na pasku była biała niezależnie od tego, w jakim kolorze ją zapisałeś:

```xml
<androidx.appcompat.widget.Toolbar
    ...
    app:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar" />
```

Ikona bez `android:title` jest niedostępna dla czytnika ekranu, więc **tytuł
wpisujesz zawsze**, nawet gdy pozycja pokazuje wyłącznie obrazek. Przy dłuższym
przytrzymaniu system pokaże ten tytuł jako podpowiedź.

---

## 7. Strzałka powrotu

Na ekranie podrzędnym pasek powinien mieć po lewej strzałkę prowadzącą wyżej.

```java
setSupportActionBar(findViewById(R.id.toolbar));
getSupportActionBar().setDisplayHomeAsUpEnabled(true);
```

Strzałka to pozycja menu o identyfikatorze systemowym `android.R.id.home`.
Masz dwa sposoby jej obsługi.

**Sposób pierwszy – przez manifest.** Wpisujesz ekran nadrzędny, tak jak
na poprzednich zajęciach:

```xml
<activity
    android:name=".AboutActivity"
    android:exported="false"
    android:parentActivityName=".MainActivity" />
```

Klasa nadrzędna obsłuży strzałkę sama – pod warunkiem, że nieobsłużone pozycje
przepuszczasz przez `super.onOptionsItemSelected(item)`.

**Sposób drugi – wprost w kodzie:**

```java
if (id == android.R.id.home) {
    finish();
    return true;
}
```

### Który wybrać

| | Przez manifest | Przez `finish()` |
|---|---|---|
| Dokąd wraca | do ekranu **wpisanego w manifeście** | do ekranu, **z którego przyszedłeś** |
| Gdy ekran otwierasz z jednego miejsca | to samo | to samo |
| Gdy z kilku miejsc | zawsze do nadrzędnego | do właściwego |
| Stan ekranu poprzedniego | zależy od sytuacji | zachowany |

W aplikacji dwuekranowej oba działają identycznie. Przy większej różnica robi się
istotna: strzałka przez manifest prowadzi **zawsze w to samo miejsce**,
niezależnie od drogi, którą użytkownik przyszedł.

> **Strzałka „w górę" to nie to samo co „wstecz".** Przycisk Wstecz cofa
> po historii – tam, gdzie byłeś przed chwilą. Strzałka prowadzi w hierarchii
> aplikacji – poziom wyżej. Gdy wejdziesz na ekran szczegółów z powiadomienia,
> Wstecz wyrzuci Cię z aplikacji, a strzałka zaprowadzi do listy, której
> wcześniej nie widziałeś.

---

## 8. Tytuł na pasku

Domyślnie pasek pokazuje nazwę aplikacji z `android:label` w manifeście.
Każdy ekran może mieć własny:

```xml
<activity
    android:name=".AboutActivity"
    android:label="@string/title_about"
    android:exported="false"
    android:parentActivityName=".MainActivity" />
```

Albo z kodu, gdy tytuł zależy od danych – wtedy w `strings.xml` trzymasz wzorzec
z miejscem na wartość, tak jak przy podsumowaniu formularza:

```xml
<string name="title_course">Kurs: %1$s</string>
```

```java
String courseName = getIntent().getStringExtra(EXTRA_COURSE);
setTitle(getString(R.string.title_course, courseName));
```

Wywołanie `setTitle` musi nastąpić **po** `setSupportActionBar`, inaczej napis
nie dotrze do paska.

---

## 9. Nawigacja dolna

Gdy aplikacja ma kilka **równorzędnych** sekcji – nie nadrzędne i podrzędne,
tylko obok siebie – zamiast kolejnych ekranów stosuje się pasek na dole.

```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottomNav"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:menu="@menu/menu_bottom" />
```

Pozycje opisujesz **tym samym rodzajem pliku menu** co menu paska górnego –
tylko bez `showAsAction`, bo wszystkie są zawsze widoczne. Każda potrzebuje ikony.

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/nav_form"
        android:icon="@drawable/ic_edit"
        android:title="@string/nav_form" />

    <item
        android:id="@+id/nav_settings"
        android:icon="@drawable/ic_settings"
        android:title="@string/nav_settings" />

</menu>
```

Obsługa to jeden listener:

```java
bottomNav.setOnItemSelectedListener(item -> {
    int id = item.getItemId();
    if (id == R.id.nav_form) {
        showSection(sectionForm);
        return true;
    } else if (id == R.id.nav_settings) {
        showSection(sectionSettings);
        return true;
    }
    return false;
});
```

Zwrócenie `true` zaznacza wybraną pozycję, `false` zostawia poprzednią zaznaczoną.

> **Nawigacja dolna nie przełącza ekranów.** To nie są osobne aktywności –
> to jedna aktywność, która pokazuje raz jedną, raz drugą część swojego układu.
> W naszym przykładzie robimy to najprościej, jak się da: dwa układy
> `LinearLayout` i przełączanie ich widoczności przez `setVisibility`.
>
> W prawdziwych aplikacjach używa się do tego **fragmentów** – samodzielnych
> kawałków ekranu z własnym układem i cyklem życia. Fragmenty poznacie przy listach;
> na razie wystarczy wiedzieć, że istnieją i po co.

Pasek dolny ma sens przy **trzech do pięciu** sekcjach. Dwie to za mało
(wystarczy przełącznik), sześć to za dużo (nie mieszczą się napisy).

---

## 10. Kompletny przykład

Aplikacja z paskiem górnym, menu, nawigacją dolną i ekranem „O aplikacji".

> **Zacznij od ikon.** Przykład używa trzech: `ic_share`, `ic_edit`
> i `ic_settings`. Utwórz je przez **Vector Asset** (rozdział 6), zanim wkleisz
> pliki menu – inaczej odwołania `@drawable/...` będą czerwone, a projekt
> się nie zbuduje. W katalogu Clip Art znajdziesz je pod nazwami *share*,
> *edit* i *settings*.

### `res/values/themes.xml` – sprawdź pierwszą linię

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Base.Theme.Pasek" parent="Theme.Material3.DayNight.NoActionBar">
    </style>

    <style name="Theme.Pasek" parent="Base.Theme.Pasek" />
</resources>
```

### `res/values/strings.xml`

```xml
<resources>
    <string name="app_name">Pasek</string>
    <string name="title_about">O aplikacji</string>

    <string name="action_share">Udostępnij</string>
    <string name="action_clear">Wyczyść</string>
    <string name="action_about">O aplikacji</string>

    <string name="nav_form">Formularz</string>
    <string name="nav_settings">Ustawienia</string>

    <string name="label_name">Imię i nazwisko</string>
    <string name="hint_name">Jan Kowalski</string>
    <string name="setting_notifications">Powiadomienia</string>
    <string name="setting_dark">Tryb ciemny</string>

    <string name="about_text">Aplikacja szkoleniowa.\nTechnik programista, klasa 5.</string>
    <string name="cleared">Wyczyszczono</string>
    <string name="share_via">Udostępnij przez</string>
    <string name="error_no_app">Brak aplikacji do obsługi tej czynności</string>
    <string name="nothing_to_share">Najpierw wpisz imię</string>
</resources>
```

### `res/menu/menu_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <item
        android:id="@+id/action_share"
        android:icon="@drawable/ic_share"
        android:title="@string/action_share"
        app:showAsAction="ifRoom" />

    <item
        android:id="@+id/action_clear"
        android:title="@string/action_clear"
        app:showAsAction="never" />

    <item
        android:id="@+id/action_about"
        android:title="@string/action_about"
        app:showAsAction="never" />

</menu>
```

### `res/menu/menu_bottom.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/nav_form"
        android:icon="@drawable/ic_edit"
        android:title="@string/nav_form" />

    <item
        android:id="@+id/nav_settings"
        android:icon="@drawable/ic_settings"
        android:title="@string/nav_settings" />

</menu>
```

### `res/layout/activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        app:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        app:titleTextColor="#FFFFFF" />

    <!-- sekcja 1: formularz -->
    <LinearLayout
        android:id="@+id/sectionForm"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/tvLabelName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/label_name" />

        <EditText
            android:id="@+id/etName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/hint_name"
            android:inputType="textPersonName"
            android:maxLines="1" />
    </LinearLayout>

    <!-- sekcja 2: ustawienia, domyślnie ukryta -->
    <LinearLayout
        android:id="@+id/sectionSettings"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:orientation="vertical"
        android:padding="16dp"
        android:visibility="gone">

        <CheckBox
            android:id="@+id/cbNotifications"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/setting_notifications" />

        <CheckBox
            android:id="@+id/cbDark"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/setting_dark" />
    </LinearLayout>

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNav"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:menu="@menu/menu_bottom" />

</LinearLayout>
```

Obie sekcje mają `layout_height="0dp"` i wagę `1`, więc **ta widoczna wypełnia
całe miejsce** między paskami. Ukryta ma `visibility="gone"`, czyli nie zajmuje
nic – gdyby było `invisible`, zabierałaby połowę wysokości na pusto.

### `res/layout/activity_about.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/about"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".AboutActivity">

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        app:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
        app:titleTextColor="#FFFFFF" />

    <TextView
        android:id="@+id/tvAbout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="16dp"
        android:text="@string/about_text"
        android:textSize="18sp" />

</LinearLayout>
```

### `AndroidManifest.xml` – fragment

```xml
<!-- wewnątrz znacznika <application>, atrybuty szablonu zostają bez zmian -->

<activity
    android:name=".MainActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity
    android:name=".AboutActivity"
    android:exported="false"
    android:label="@string/title_about"
    android:parentActivityName=".MainActivity" />
```

### `MainActivity.java`

```java
package com.example.pasek;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private View sectionForm, sectionSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);          // pasek staje się paskiem aplikacji

        etName = findViewById(R.id.etName);
        sectionForm = findViewById(R.id.sectionForm);
        sectionSettings = findViewById(R.id.sectionSettings);

        setUpBottomNavigation();
    }

    private void setUpBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_form) {
                showSection(true);
                return true;
            } else if (id == R.id.nav_settings) {
                showSection(false);
                return true;
            }
            return false;
        });
    }

    private void showSection(boolean form) {
        sectionForm.setVisibility(form ? View.VISIBLE : View.GONE);
        sectionSettings.setVisibility(form ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            share();
            return true;
        } else if (id == R.id.action_clear) {
            clearForm();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearForm() {
        etName.setText("");
        Toast.makeText(this, R.string.cleared, Toast.LENGTH_SHORT).show();
    }

    private void share() {
        String text = etName.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, R.string.nothing_to_share, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show();
        }
    }
}
```

### `AboutActivity.java`

```java
package com.example.pasek;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    // strzałka powrotu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();                       // wróć do ekranu, z którego przyszliśmy
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
```

Ekran „O aplikacji" **nie nadpisuje `onCreateOptionsMenu`**, więc nie ma menu –
tylko strzałkę i tytuł. Każdy ekran decyduje o swoim pasku osobno.

---

## 11. Częste błędy

| Objaw | Przyczyna | Rozwiązanie |
|---|---|---|
| *This Activity already has an action bar* | motyw nie jest `NoActionBar`, a dodałeś `Toolbar` | dopisz `.NoActionBar` w `themes.xml` |
| Menu w ogóle się nie pojawia | brak `onCreateOptionsMenu` albo zwraca `false` | nadpisz i zwróć `true` |
| Pozycja z `always` ląduje pod kropkami | `android:showAsAction` zamiast `app:` | zmień przedrostek, sprawdź `xmlns:app` |
| *constant expression required* | `switch` po `item.getItemId()` | `if / else if` |
| `setSupportActionBar` nie przyjmuje paska | zaimportowany `android.widget.Toolbar` | zamień na `androidx.appcompat.widget.Toolbar` |
| Strzałka jest, ale nic nie robi | brak obsługi `android.R.id.home` i brak `parentActivityName` | dodaj jedno albo drugie |
| Strzałki w ogóle nie widać | brak `setDisplayHomeAsUpEnabled(true)` | dopisz po `setSupportActionBar` |
| `NullPointerException` przy `getSupportActionBar()` | wywołane przed `setSupportActionBar` | kolejność w `onCreate` |
| `setTitle` nie zmienia napisu | wywołane przed `setSupportActionBar` | jak wyżej |
| Pasek dolny bez napisów | brak `android:title` w pozycjach menu | tytuł jest obowiązkowy |
| Po przełączeniu sekcji zostaje pusta przestrzeń | ukrywanie przez `invisible` | użyj `gone` |
| Ikona niewidoczna mimo `ifRoom` | pasek zajęty przez długi tytuł | skróć tytuł albo daj `always` |

---

## 12. Ściąga

**Układ**

```xml
<androidx.appcompat.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    android:background="?attr/colorPrimary" />
```

**Aktywność**

| Zapis | Znaczenie |
|---|---|
| `setSupportActionBar(toolbar)` | zgłoszenie paska systemowi |
| `getSupportActionBar().setDisplayHomeAsUpEnabled(true)` | strzałka powrotu |
| `setTitle("...")` | tytuł; **po** `setSupportActionBar` |
| `getMenuInflater().inflate(R.menu.x, menu)` | wczytanie menu z pliku |
| `item.getItemId()` | która pozycja kliknięta |
| `android.R.id.home` | identyfikator strzałki powrotu |

**Menu**

| Atrybut | Uwaga |
|---|---|
| `android:id` | po nim rozpoznajesz kliknięcie |
| `android:title` | obowiązkowy, nawet przy samej ikonie |
| `android:icon` | widoczna tylko na pasku |
| `app:showAsAction` | **`app:`**, nie `android:` |

Wartości: `never` (domyślna), `ifRoom`, `always`, `withText`.

**Metody do nadpisania**

```java
@Override public boolean onCreateOptionsMenu(Menu menu) { ... return true; }
@Override public boolean onOptionsItemSelected(MenuItem item) { ... return super....; }
```

W `onOptionsItemSelected`: `if / else if`, `return true` przy obsłużonej pozycji,
`return super.onOptionsItemSelected(item)` na końcu.

---

## 13. Zadania

Jak zawsze: osobne repozytorium, `README.md` ze zrzutami, commit po każdym kroku,
teksty w `strings.xml`.

### Zadanie 1 – pasek, menu i nawigacja dolna

Zbuduj aplikację z rozdziału 10. Oddajesz w pięciu commitach:

1. `chore: motyw NoActionBar i Toolbar w ukladzie`
2. `feat: menu opcji z trzema pozycjami`
3. `feat: obsluga pozycji menu`
4. `feat: ekran O aplikacji ze strzalka powrotu`
5. `feat: nawigacja dolna z dwiema sekcjami`

W `README.md` odpowiedz na dwa pytania:

- **Co się stanie, jeśli w `menu_main.xml` zamienisz `app:showAsAction`
  na `android:showAsAction`?** Sprawdź w praktyce, nie zgaduj.
- **Dlaczego ostatnia linia `onOptionsItemSelected` to
  `return super.onOptionsItemSelected(item)`, a nie `return false`?**

### Zadanie 2 – pasek we własnej aplikacji

Weź **swoją aplikację dwuekranową z poprzednich zajęć** i dodaj do niej
pełną nawigację.

**Wymagania:**

1. `Toolbar` na **obu** ekranach, każdy z własnym tytułem.
2. Menu opcji na ekranie głównym: **minimum trzy pozycje**, w tym
   co najmniej jedna z ikoną i `showAsAction="ifRoom"`.
3. Ikony utworzone przez **Vector Asset**, nie pobrane z internetu.
4. Strzałka powrotu na ekranie podrzędnym, działająca.
5. Jedna pozycja menu otwiera **nowy ekran** „O aplikacji",
   jedna wykonuje **czynność** na bieżącym ekranie, jedna uruchamia
   **intencję niejawną**.
6. Tytuł ekranu podrzędnego ustawiony z kodu i **zawierający dane**
   przekazane z pierwszego ekranu – na przykład „Zamówienie: pizza".

**O co zapytam:** czym różni się `ActionBar` od `Toolbar` i skąd wiesz,
który masz w swoim projekcie.

### Zadanie 3 – ekran ustawień

Rozbuduj sekcję ustawień z nawigacji dolnej o **cztery** przełączniki:
powiadomienia, tryb ciemny, dźwięk, automatyczny zapis.

Pod nimi umieść `TextView` pokazujący na bieżąco, które opcje są włączone –
aktualizowany przy każdej zmianie, bez żadnego przycisku.

Dołóż do menu paska górnego pozycję **„Przywróć domyślne"**, która po
potwierdzeniu w `AlertDialog` ustawia wszystkie przełączniki w stan wyjściowy.

Na razie ustawienia **znikają po zamknięciu aplikacji** – to normalne
i naprawimy to na zajęciach o zapisie danych. Napisz w `README.md`, dlaczego
tak się dzieje.

### Zadanie 4 – `[bez AI]`

Bez agenta i bez czatu, wyłącznie ten materiał i dokumentacja.

Napisz aplikację **„Licznik z paskiem"**:

- `Toolbar` z tytułem „Licznik",
- duża liczba na środku ekranu,
- menu z trzema pozycjami: **„+1"** i **„−1"** jako ikony na pasku
  (`showAsAction="always"`) oraz **„Zeruj"** pod trzema kropkami,
- „Zeruj" pyta o potwierdzenie w `AlertDialog`,
- tytuł paska zmienia się na „Licznik: N" przy każdej zmianie wartości,
- licznik nie schodzi poniżej zera – przy próbie `Toast`.

Commit z prefiksem `[bez AI]`. Na następnych zajęciach opowiadasz,
co było trudniejsze: menu czy zmiana tytułu paska.
