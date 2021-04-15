package main.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.model.enums.GlobalSettingsValue;
import org.hibernate.annotations.ColumnTransformer;
import javax.persistence.*;

@Data
@Entity
@Table(name = "global_setting")
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "gs_id_gen", sequenceName = "gs_seq")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GlobalSetting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int id;

  @Column(nullable = false)
  @ColumnTransformer(read = "UPPER(code)")
  String code;

  @Column(nullable = false)
  String name;

  @Column(nullable = false)
  @ColumnTransformer(read = "UPPER(value)")
  @Enumerated(EnumType.STRING)
  GlobalSettingsValue value;
}