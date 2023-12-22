package mate.academy.rickandmorty.service.impl;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import mate.academy.rickandmorty.dto.external.CharacterResponseDto;
import mate.academy.rickandmorty.dto.internal.CharacterDto;
import mate.academy.rickandmorty.mapper.CharacterMapper;
import mate.academy.rickandmorty.model.Character;
import mate.academy.rickandmorty.repository.CharacterRepository;
import mate.academy.rickandmorty.service.CharacterClient;
import mate.academy.rickandmorty.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CharacterServiceImpl implements CharacterService {
    private static Long maxId;
    private final CharacterClient characterClient;
    private final CharacterRepository characterRepository;
    private final CharacterMapper characterMapper;
    private final Random random = new Random();

    @Autowired
    public CharacterServiceImpl(CharacterClient characterClient,
                                CharacterRepository characterRepository,
                                CharacterMapper characterMapper) {
        this.characterClient = characterClient;
        this.characterRepository = characterRepository;
        this.characterMapper = characterMapper;
    }

    @PostConstruct
    private void getAllCharacter() {
        List<CharacterResponseDto> allCharacters = characterClient.getAllCharacters();
        characterRepository.saveAll(allCharacters.stream()
                .map(characterMapper::toModel)
                .toList());
        maxId = allCharacters.get(allCharacters.size() - 1).id();
    }

    @Override
    public CharacterDto getRandomCharacter() {
        long randomLong = random.nextLong(maxId + 1);
        Character character = characterRepository.findById(randomLong)
                .orElseThrow(RuntimeException::new);
        return characterMapper.toDto(character);
    }

    @Override
    public List<CharacterDto> getCharactersByName(String name) {
        return characterRepository.findCharactersByNameContainsIgnoreCase(name).stream()
                .map(characterMapper::toDto)
                .toList();
    }
}