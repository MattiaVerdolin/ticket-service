package ch.supsi.webapp.tickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La classe Success è un semplice DTO (Data Transfer Object) utilizzato per rappresentare
 * lo stato di successo o fallimento di un'operazione. È spesso impiegata nelle risposte
 * delle API REST per fornire un feedback chiaro e standardizzato al client.
 *
 * Campi:
 * - `success` (boolean): Indica se l'operazione è stata eseguita con successo (true) o meno (false).
 *
 * Annotazioni:
 * - `@Data`: Genera automaticamente getter, setter, metodi `toString`, `equals`, e `hashCode`.
 * - `@NoArgsConstructor`: Crea un costruttore vuoto.
 * - `@AllArgsConstructor`: Crea un costruttore che accetta tutti i campi della classe.
 *
 * Esempio d'uso:
 * - Per rappresentare il successo o fallimento di un'operazione come l'eliminazione di un ticket
 *   in un'API REST.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Success {
    /**
     * Indica lo stato dell'operazione:
     * - true: Operazione riuscita.
     * - false: Operazione fallita.
     */
    private boolean success;
}
