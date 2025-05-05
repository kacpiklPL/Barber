# System Rezerwacji dla Barber Shopu

Projekt webowej aplikacji backendowej służącej do zarządzania rezerwacjami w salonie barberskim. 
Aplikacja umożliwia klientom dokonywanie rezerwacji wizyt, a administratorowi – zarządzanie dostępnością usług, terminarzem oraz kontrolą wszystkich pracowników.

## Funkcje
- Tworzenie i zarządzanie klientami
- Tworzenie i zarządzanie pracownikami
- Tworzenie i zarządzanie usługami osobno dla danego pracownika (np. strzyżenie, golenie)

## Planowane funkcjonalności
- Tworzenie i zarządzanie godzinami pracy pracownika (Niestandardowe godziny pracy)
- Tworzenie i zarządzanie rezerwacjami
- Płatność z góry za wizytę z użyciem Stripe
- System SMS, który będzie przypominał o zbliżającej się wizycie
- Raport pracowników ze statystykami

## Technologia
- **Java 17**
- **Spring Boot**
- **MySQL**
- **Maven**

## Instalacja i uruchomienie
1. Sklonuj repozytorium:
git clone https://github.com/kacpiklPL/Barber.git
2. Skonfiguruj bazę danych i plik `application.properties`
3. Uruchom aplikację:
./mvnw spring-boot:run

## Status projektu
🔧 Projekt w fazie rozwoju.
